import { useState } from 'react';

import { Panel } from '../../components/layout/Panel';
import type { CreatePostInput, PostStatus } from '../../types/cms';
import { CATEGORY_OPTIONS, LAYOUT_OPTIONS, STATUS_TEXT } from '../../utils/cms-labels';

interface NewPostViewProps {
  submitting: boolean;
  onCancel: () => void;
  onSubmit: (payload: CreatePostInput) => Promise<void>;
  onMessage: (message: string) => void;
}

export function NewPostView({
  submitting,
  onCancel,
  onSubmit,
  onMessage,
}: NewPostViewProps) {
  const [form, setForm] = useState<CreatePostInput>({
    title: '',
    category: 'Tin tức',
    status: 'draft',
    content: '',
    metaDescription: '',
    layout: 'article-story',
  });

  function update<K extends keyof CreatePostInput>(key: K, value: CreatePostInput[K]) {
    setForm((previous) => ({
      ...previous,
      [key]: value,
    }));
  }

  async function submit(statusOverride?: PostStatus) {
    const payload = {
      ...form,
      title: form.title.trim(),
      content: form.content.trim(),
      metaDescription: form.metaDescription.trim(),
      status: statusOverride ?? form.status,
    };

    if (!payload.title) {
      onMessage('Vui lòng nhập tiêu đề bài viết.');
      return;
    }

    await onSubmit(payload);
  }

  return (
    <div className="page-stack">
      <Panel title="Tạo bài viết mới">
        <div className="form-grid">
          <label className="field">
            <span>Tiêu đề</span>
            <input
              className="input"
              onChange={(event) => update('title', event.target.value)}
              placeholder="Nhập tiêu đề bài viết..."
              value={form.title}
            />
          </label>

          <div className="form-grid form-grid--two">
            <label className="field">
              <span>Danh mục</span>
              <select
                className="input"
                onChange={(event) => update('category', event.target.value)}
                value={form.category}
              >
                {CATEGORY_OPTIONS.map((option) => (
                  <option key={option} value={option}>
                    {option}
                  </option>
                ))}
              </select>
            </label>

            <label className="field">
              <span>Trạng thái</span>
              <select
                className="input"
                onChange={(event) => update('status', event.target.value as PostStatus)}
                value={form.status}
              >
                {Object.entries(STATUS_TEXT).map(([value, label]) => (
                  <option key={value} value={value}>
                    {label}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <div className="form-grid form-grid--two">
            <label className="field">
              <span>Layout</span>
              <select
                className="input"
                onChange={(event) => update('layout', event.target.value)}
                value={form.layout}
              >
                {LAYOUT_OPTIONS.map((option) => (
                  <option key={option} value={option}>
                    {option}
                  </option>
                ))}
              </select>
            </label>

            <label className="field">
              <span>Meta description</span>
              <input
                className="input"
                onChange={(event) => update('metaDescription', event.target.value)}
                placeholder="Nếu để trống, SEO Plugin sẽ tự sinh."
                value={form.metaDescription}
              />
            </label>
          </div>

          <label className="field">
            <span>Nội dung</span>
            <textarea
              className="input input--textarea"
              onChange={(event) => update('content', event.target.value)}
              placeholder="Viết nội dung bài viết..."
              value={form.content}
            />
          </label>
        </div>

        <div className="form-actions">
          <button className="button button--secondary" onClick={onCancel} type="button">
            Hủy
          </button>
          <button
            className="button button--secondary"
            disabled={submitting}
            onClick={() => void submit('draft')}
            type="button"
          >
            Lưu nháp
          </button>
          <button className="button" disabled={submitting} onClick={() => void submit()} type="button">
            {submitting ? 'Đang gửi...' : 'Đăng bài'}
          </button>
        </div>
      </Panel>
    </div>
  );
}
