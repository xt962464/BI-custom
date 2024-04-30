import {ActionType, ModalForm, ProColumns, ProFormSelect, ProFormTextArea} from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import {useRef, useState} from 'react';
import {
  deleteEvaluatePOST,
  updateEvaluatePOST
} from "@/services/BI_Front/postEvaluateController";
import {Button, message} from "antd";
import {
  addCommentPOST,
  deleteCommentPOST,
  listCommentByPageUsingPOST,
  updateCommentPOST
} from "@/services/BI_Front/postCommentController";
import {listAllChartUsingPOST} from "@/services/BI_Front/chartController";
export const waitTimePromise = async (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, time);
  });
};
export const waitTime = async (time: number = 100) => {
  await waitTimePromise(time);
};



/**
 * 修改评价内容
 * @param id 评价id
 * @param conetnt 内容
 */
const updateContent = async (id: string, content: API.Comment) => {
  const params: API.CommentUpdateRequest = {
    id: id,
    userId: content.userId,
    content: content.content
  }
  try{
    const resp = await updateCommentPOST(params)
    if (resp.data) {
      message.success("保存成功");
    } else {
      message.error("保存失败");
    }
  } catch (e: any) {
    message.error("保存失败，" + e.message);
  }
}

/**
 * 增加留言
 */
const addComment = async ( content: string) => {
  try {
    const params: API.CommentAddRequest = {
      content: content
    }
    const res = await addCommentPOST(params);
    console.log('res', res)
    if (res.code == 0) {
      message.success("添加成功");
    } else {
      message.error("添加失败，" + res.message);
    }
  } catch (e: any) {
    message.error("添加失败，" + e.message);
  }
}

/**
 * 删除
 * @param id
 * @param callback 回调函数
 */
const deleteHandler = async (id: string, callback = ()=>{}) => {
  const params: API.DeleteRequest = {
    id: id
  }
  try{
    const resp = await deleteCommentPOST(params)
    if (resp.data) {
      message.success("删除成功");
      callback();
    } else {
      message.error("删除失败");
    }
  } catch (e: any) {
    message.error("删除失败，" + e.message);
  }
}


const columns: ProColumns<API.Comment>[] = [
  {
    dataIndex: 'index',
    valueType: 'indexBorder',
    width: 48,
  },
  {
    title: '用户',
    dataIndex: 'userName',
    copyable: true,
    editable: false, // 设置为只读
    render: (_, record) => record?.user?.userAccount
  },
  {
    title: '留言内容',
    dataIndex: 'content',
    copyable: false,
    ellipsis: true,
  },
  {
    title: '留言时间',
    dataIndex: 'createTime',
    valueType: 'dateTime',
    editable: false, // 设置为只读
  },
  {
    title: '操作',
    valueType: 'option',
    key: 'option',
    render: (text, record, _, action) => [
      <a
        key="editable"
        onClick={() => {
          action?.startEditable?.(record.id);
        }}
      >
        编辑
      </a>,
      <TableDropdown
        key="actionGroup"
        onSelect={() => deleteHandler(record.id, ()=>{
          action?.reload();
        })}
        menus={[
          { key: 'delete', name: '删除' },
        ]}
      />,
    ],
  },
];

export default () => {
  const actionRef = useRef<ActionType>();
  const [modalVisit, setModalVisit] = useState<boolean>(false);
  const [pageSize, setPageSize] = useState<number>();
  const [pageTotal, setPageTotal] = useState<number>();
  const [total, setTotal] = useState<number>();
  const [current, setCurrent] = useState<number>();

  return (
    <>
    <ProTable<API.Comment>
      columns={columns}
      actionRef={actionRef}
      cardBordered
      request={async (params: API.CommentQueryRequest, sort, filter) => {
        console.log(sort, filter);
        const responsePage = await listCommentByPageUsingPOST(params);
        const pageDate = responsePage.data;
        setPageSize(pageDate.size);
        setPageTotal(pageDate.pages);
        setCurrent(pageDate.current);
        setTotal(pageDate.total);
        // @ts-ignore
        let dataList: any = [];
        if (pageDate) {
          dataList = pageDate.records;
        }
        return {
          data: dataList,
          success: true,
          current: pageDate.current,
          pageSize: pageDate.size,
          pages: pageDate.pages,
          // 不传会使用 data 的长度，如果是分页一定要传
          total: pageDate.total,
        }
      }}
      editable={{
        type: 'multiple',
        onSave: async (rowKey, data) => {
          console.log('保存', rowKey, data);
          updateContent(data.id, data);
        },
      }}
      rowKey="id"
      search={{
        labelWidth: 'auto',
        optionRender: ({searchText, resetText}, {form}, dom) => [
          <Button key="add-btn" type="primary" onClick={setModalVisit}>添加</Button>,
          <Button
            key="searchText"
            type="primary"
            onClick={() => {
              // console.log(params);
              form?.submit();
            }}
          >
            {searchText}
          </Button>,
          <Button
            key="resetText"
            onClick={() => {
              form?.resetFields();
            }}
          >
            {resetText}
          </Button>
        ]
      }}
      options={{
        setting: {
          listsHeight: 400,
        },
      }}
      form={{
        // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
        syncToUrl: (values, type) => {
          if (type === 'get') {
            return {
              ...values,
              created_at: [values.startTime, values.endTime],
            };
          }
          return values;
        },
      }}
      pagination={{
        current: current,
        onChange: (page) => console.log(page),
      }}
      dateFormatter="string"
      headerTitle="留言列表"
    />
      <ModalForm
        title="添加留言"
        key={"add-table"}
        visible={modalVisit}
        onFinish={async (values) => {
          console.log(values);
          if (values.content) {
            await addComment(values.content);
            return true;
          }
          return false;
        }}
        onVisibleChange={setModalVisit}
      >
        <ProFormTextArea
          name="content"
          placeholder="请输入留言内容"
          style={{ height: 120, resize: 'none' }}
        />
      </ModalForm>
    </>
  );
};
