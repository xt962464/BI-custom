// @ts-ignore
/* eslint-disable */
import { request } from 'umi';



/** listEvaluateByPageUsingPOST POST /api/evaluate/list/page */
export async function listEvaluateByPageUsingPOST(
  body: API.EvaluateQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageEvaluate_>('/api/evaluate/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** addEvaluate POST /api/evaluate/add */
export async function addEvaluatePOST(body: API.EvaluateAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponse_>('/api/evaluate/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新修改 POST /api/evaluate/update */
export async function updateEvaluatePOST(body: API.EvaluateUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponse_>('/api/evaluate/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除 POST /api/evaluate/delete */
export async function deleteEvaluatePOST(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/evaluate/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
