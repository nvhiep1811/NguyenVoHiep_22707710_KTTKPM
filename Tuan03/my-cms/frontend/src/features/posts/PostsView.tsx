import { useDeferredValue, useState } from 'react';

import { Panel } from '../../components/layout/Panel';
import { Badge } from '../../components/ui/Badge';
import { Table } from '../../components/ui/Table';
import type { PostRecord } from '../../types/cms';
import { STATUS_TEXT } from '../../utils/cms-labels';

interface PostsViewProps {
  posts: PostRecord[];
  onCreateNew: () => void;
}

export function PostsView({ posts, onCreateNew }: PostsViewProps) {
  const [query, setQuery] = useState('');
  const deferredQuery = useDeferredValue(query.trim().toLowerCase());

  const filteredPosts = posts.filter((post) => {
    if (!deferredQuery) {
      return true;
    }

    return [post.title, post.author, post.category, post.status].some((value) => {
      return value.toLowerCase().includes(deferredQuery);
    });
  });

  return (
    <div className="page-stack">
      <Panel
        title="Danh sách bài viết"
        aside={
          <div className="toolbar">
            <input
              className="input"
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Tìm theo tiêu đề, tác giả, danh mục..."
              value={query}
            />
            <button className="button" onClick={onCreateNew} type="button">
              + Bài mới
            </button>
          </div>
        }
      >
        <Table
          heads={['Tiêu đề', 'Danh mục', 'Tác giả', 'Trạng thái', 'Ngày']}
          rows={filteredPosts.map((post) => [
            <div key={`${post.id}-title`}>
              <strong>{post.title}</strong>
              <p className="cell-copy">{post.excerpt}</p>
            </div>,
            post.category,
            post.author,
            <Badge key={`${post.id}-status`} tone={post.status}>
              {STATUS_TEXT[post.status]}
            </Badge>,
            post.date,
          ])}
        />
      </Panel>
    </div>
  );
}
