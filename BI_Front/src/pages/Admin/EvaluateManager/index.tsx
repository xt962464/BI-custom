import {
  ActionType,
  ModalForm,
  ProColumns,
  ProForm,
  ProFormSelect,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import {useRef, useState} from 'react';
import {
  addEvaluatePOST,
  deleteEvaluatePOST,
  listEvaluateByPageUsingPOST,
  updateEvaluatePOST
} from "@/services/BI_Front/postEvaluateController";
import {Button, message} from "antd";
import {listAllChartUsingPOST} from "@/services/BI_Front/chartController";
import TextArea from "antd/es/input/TextArea";
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
const updateContent = async (id: string, content: API.Evaluate) => {
  const params: API.EvaluateUpdateRequest = {
    id: id,
    userId: content.userId,
    chartId: content.chartId,
    content: content.content
  }
  try{
    const resp = await updateEvaluatePOST(params)
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
 * 删除
 * @param id
 * @param callback 回调函数
 */
const deleteHandler = async (id: string, callback = ()=>{}) => {
  const params: API.DeleteRequest = {
    id: id
  }
  try{
    const resp = await deleteEvaluatePOST(params)
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

/**
 * 提交评价
 */
const submitEvaluate = async (chartId: string, content: string) => {
  try {
    const params: API.EvaluateAddRequest = {
      chartId: String(chartId),
      content: content
    }
    const res = await addEvaluatePOST(params);
    console.log('res', res)
    if (res.code == 0) {
      message.success("评价成功");
    } else {
      message.error("评价失败，" + res.message);
    }
  } catch (e: any) {
    message.error("评价失败，" + e.message);
  }
}

const columns: ProColumns<API.Evaluate>[] = [
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
    title: '图表',
    dataIndex: 'chartName',
    copyable: true,
    editable: false, // 设置为只读
    render: (_, record) => record?.chart?.name
  },
  {
    title: '评价内容',
    dataIndex: 'content',
    copyable: false,
    ellipsis: true,
  },
  {
    title: '评价时间',
    dataIndex: 'createTime',
    valueType: 'dateTime',
    readonly: true,
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
      <ProTable<API.Evaluate>
        key={"table-list"}
        columns={columns}
        actionRef={actionRef}
        cardBordered
        request={async (params: API.EvaluateQueryRequest, sort, filter) => {
          console.log(sort, filter);
          const responsePage = await listEvaluateByPageUsingPOST(params);
          const pageDate = responsePage.data;
          setPageSize(pageDate.size);
          setPageTotal(pageDate.pages);
          setCurrent(pageDate.current);
          setTotal(pageDate.total);
          // @ts-ignore
          let evaluateList: any = [];
          if (pageDate) {
            evaluateList = pageDate.records;
          }
          return {
            data: evaluateList,
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
        headerTitle="评价列表"
      />
      <ModalForm
        title="添加评价"
        key={"add-table"}
        visible={modalVisit}
        onFinish={async (values) => {
          console.log(values);
          if (values.chartId && values.content) {
            await submitEvaluate(values.chartId, values.content);
            return true;
          }
          return false;
        }}
        onVisibleChange={setModalVisit}
      >
          <ProFormSelect
            width="md"
            request={async () => {
              const resp = await listAllChartUsingPOST({});
              const dataList =[]
              if (resp.code == 0 && resp.data){
                for (const d of resp.data) {
                  dataList.push({
                    label: d.name,
                    value: d.id
                  })
                }
              }
              return dataList;
            }}
            name="chartId"
            label="图表"
          />
        <ProFormTextArea
          name="content"
          placeholder="请输入评价"
          style={{ height: 120, resize: 'none' }}
        />
      </ModalForm>
    </>
  );
};
