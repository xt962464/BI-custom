// @ts-ignore
/* eslint-disable */
import { request } from 'umi';



/** listCommentByPageUsingPOST POST /api/comment/list/page */
export async function listCommentByPageUsingPOST(
  body: API.CommentQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageComment_>('/api/comment/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** addComment POST /api/comment/add */
export async function addCommentPOST(body: API.CommentAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/comment/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}


/** 更新修改 POST /api/comment/update */
export async function updateCommentPOST(body: API.CommentUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/comment/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除 POST /api/evaluate/update */
export async function deleteCommentPOST(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/comment/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
