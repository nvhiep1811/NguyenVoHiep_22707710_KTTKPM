import { useEffect, useState, useTransition } from 'react'

import { Sidebar } from './components/layout/Sidebar'
import { TopBar } from './components/layout/TopBar'
import { Toast } from './components/ui/Toast'
import { DashboardView } from './features/dashboard/DashboardView'
import { PluginsView } from './features/plugins/PluginsView'
import { NewPostView } from './features/posts/NewPostView'
import { PostsView } from './features/posts/PostsView'
import { RolesView } from './features/roles/RolesView'
import { ErrorState, LoadingState } from './features/shared/StateViews'
import { UsersView } from './features/users/UsersView'
import { useCmsData } from './hooks/useCmsData'
import type { CreatePostInput, PageId } from './types/cms'
import { PAGE_TITLES } from './utils/cmsNavigation'

function App() {
  const cms = useCmsData()
  const [page, setPage] = useState<PageId>('dashboard')
  const [toast, setToast] = useState('')
  const [isPending, startTransition] = useTransition()
  const [submittingPost, setSubmittingPost] = useState(false)
  const [togglingSlug, setTogglingSlug] = useState<string | null>(null)

  useEffect(() => {
    if (!toast) {
      return undefined
    }

    const timer = window.setTimeout(() => setToast(''), 2600)
    return () => window.clearTimeout(timer)
  }, [toast])

  const dashboard = cms.dashboard
  const roles = cms.roles
  const activePlugins = cms.plugins.filter((plugin) => plugin.enabled).length

  function showToast(message: string) {
    setToast(message)
  }

  function navigate(nextPage: PageId) {
    startTransition(() => {
      setPage(nextPage)
    })
  }

  function handleTopAction() {
    if (page === 'plugins') {
      showToast('Bật hoặc tắt plugin trực tiếp trong danh sách.')
      return
    }

    if (page === 'users') {
      showToast('Backend hiện mới mở API đọc user. Có thể nối thêm create user sau.')
      return
    }

    navigate('new-post')
  }

  async function handleSubmitPost(payload: CreatePostInput) {
    try {
      setSubmittingPost(true)
      const created = await cms.createPost(payload)
      showToast(`Đã tạo bài viết "${created.title}".`)
      navigate('posts')
    } catch (error) {
      showToast(error instanceof Error ? error.message : 'Không thể tạo bài viết.')
    } finally {
      setSubmittingPost(false)
    }
  }

  async function handleTogglePlugin(slug: string) {
    try {
      setTogglingSlug(slug)
      const updated = await cms.togglePlugin(slug)
      showToast(updated.enabled ? `${updated.name} đã được bật.` : `${updated.name} đã được tắt.`)
    } catch (error) {
      showToast(error instanceof Error ? error.message : 'Không thể cập nhật plugin.')
    } finally {
      setTogglingSlug(null)
    }
  }

  let content = <LoadingState />

  if (cms.error && !dashboard) {
    content = <ErrorState message={cms.error} onRetry={cms.refresh} />
  } else if (!dashboard || !roles) {
    content = <LoadingState />
  } else if (page === 'dashboard') {
    content = <DashboardView dashboard={dashboard} plugins={cms.plugins} posts={cms.posts} />
  } else if (page === 'posts') {
    content = <PostsView onCreateNew={() => navigate('new-post')} posts={cms.posts} />
  } else if (page === 'new-post') {
    content = (
      <NewPostView
        onCancel={() => navigate('posts')}
        onMessage={showToast}
        onSubmit={handleSubmitPost}
        submitting={submittingPost}
      />
    )
  } else if (page === 'users') {
    content = <UsersView users={cms.users} />
  } else if (page === 'roles') {
    content = <RolesView roles={roles} />
  } else {
    content = (
      <PluginsView
        onToggle={handleTogglePlugin}
        plugins={cms.plugins}
        togglingSlug={togglingSlug}
      />
    )
  }

  return (
    <div className="app-shell">
      <Sidebar
        activePage={page}
        installedPlugins={activePlugins}
        onNavigate={navigate}
      />

      <main className="workspace">
        <TopBar
          actionLabel={page === 'plugins' ? 'Quản lý plugin' : '+ Tạo mới'}
          isPending={isPending}
          isRefreshing={cms.loading}
          onAction={handleTopAction}
          onRefresh={cms.refresh}
          title={PAGE_TITLES[page]}
        />

        <section className="workspace__body">
          {cms.error && dashboard ? <div className="inline-alert">{cms.error}</div> : null}
          {content}
        </section>
      </main>

      <Toast message={toast} />
    </div>
  )
}

export default App
