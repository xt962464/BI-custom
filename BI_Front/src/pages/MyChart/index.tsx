import React, { useEffect, useState } from 'react';
import { listMyChartByPageUsingPOST } from "@/services/BI_Front/chartController";
import {Avatar, Card, List, message, Result, Button, Input, Divider} from 'antd';
import ReactECharts from "echarts-for-react";
import {useModel} from "@@/plugin-model/useModel";
import Search from "antd/es/input/Search";
import {addEvaluatePOST} from "@/services/BI_Front/postEvaluateController";

const { Meta } = Card;
const { TextArea } = Input;
const MyChartPage: React.FC = () => {

  const initSearchParams = {
    current: 1,
    pageSize: 6,
    sortField: 'createTime',
    sortOrder: 'desc',
  }

  const [ searchParams, setSearchParams ] = useState<API.ChartQueryRequest>({...initSearchParams});
  const [ chartList, setChartList ] = useState<API.Chart[]>();
  const [ total, setTotal ] = useState<number>(0);
  const [ loading, setLoading ] = useState<boolean>(true);
  const [ toEvaluateId, setToEvaluateId ] = useState<string>();
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const [evaluateText, setEvaluateText] = useState('');

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await listMyChartByPageUsingPOST(searchParams);
      if (res.data) {
        //隐藏图表的title
        if (res.data.records) {
          res.data.records.forEach(data => {
            if (data.status === 'success') {
              const chartOption = JSON.parse(data.genChart ?? '{}');
              chartOption.title = undefined;
              data.genChart = JSON.stringify(chartOption);
            }
          })
        }
        setChartList(res.data.records ?? []);
        setTotal(res.data.total ?? 0);
      } else {
        message.error("获取我的图表失败");
      }

    } catch (e: any) {
      message.error("获取我的图表失败，" + e.message);
    }
    setLoading(false);
  }

  useEffect(() => {
    loadData();
  }, [searchParams]);

  /**
   * 提交评价
   */
  const submitEvaluate = async () => {
    console.log('评价', evaluateText);
    setLoading(true);
    try {
      const params: API.EvaluateAddRequest = {
        userId: String(currentUser?.id),
        chartId: String(toEvaluateId),
        content: evaluateText
      }
      const res = await addEvaluatePOST(params);
      console.log('res', res)
      if (res.code == 0) {
        message.success("评价成功");
        setEvaluateText('');
        setToEvaluateId('');
        await loadData();
      } else {
        message.error("评价失败，" + res.message);
      }
    } catch (e: any) {
      message.error("评价失败，" + e.message);
    }
    setLoading(false);
  }
  /**
   * 评价输入框输入事件
   * @param e
   */
  const evaluateTextChange = (e: any) => {
    setEvaluateText(e.target.value);
  }

  return (
    <div className="my-chart-page">
      <div>
        <Search placeholder={'请输入图表名称'} enterButton loading={loading} onSearch={(value) => {
          setSearchParams({
            ...initSearchParams,
            name: value,
          })
        }}/>
      </div>
      <div className="margin-16"/>
      <List
        grid={{
          gutter: 16,
          xs: 1,
          sm: 1,
          md: 1,
          lg: 2,
          xl: 2,
          xxl: 2,
        }}
        pagination={{
          onChange: (page, pageSize) => {
            setSearchParams({
              ...searchParams,
              current: page,
              pageSize,
            })
          },
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total: total,
        }}
        loading={loading}
        dataSource={chartList}
        renderItem={(item) => (
          <List.Item key={item.id}>
            <Card>
              <List.Item.Meta
                avatar={<Avatar src={currentUser && currentUser.userAvatar} />}
                title={item.name}
                description={item.charType ? ('图表类型：' + item.charType) : undefined}
              />
              <>
                {
                  item.status === 'success' && <>
                    <div style={{marginBottom: 16}}/>
                    {'分析目标' + item.goal}
                    <div style={{marginBottom: 16}}/>
                    <ReactECharts option={JSON.parse(item.genChart ?? '{}')} />
                  </>
                }
                {
                  item.status === 'wait' && <>
                    <Result
                      status="info"
                      title="排队中......"
                      subTitle={item.execMessage ?? '当前生成请求较多，请耐心等候'} />
                  </>
                }
                {
                  item.status === 'running' && <>
                    <Result
                      status="info"
                      title="图表生成中......"
                      subTitle={item.execMessage ?? "AI正在为你生成图表..."} />
                  </>
                }
                {
                  item.status === 'failed' && <>
                    <Result
                      status="error"
                      title="图表生成失败"
                      subTitle={item.execMessage} />
                  </>
                }
                {
                  item.id === toEvaluateId && <>
                    <div>
                      <TextArea value={evaluateText} onChange={evaluateTextChange} rows={4} placeholder="请输入评价" maxLength={255}></TextArea>
                      <div style={{marginTop: '20px'}}>
                          <Button type="primary" onClick={submitEvaluate}>提交</Button>
                      </div>
                    </div>
                  </>
                }
              </>
              {
                item.id !== toEvaluateId && !item.isEvaluate && <>
                    <Meta title="我的评价"
                          description={<Button type="link" onClick={()=>{
                            setToEvaluateId(item?.id)
                          }}>还未评价,去评价</Button>}
                    />
                </>
              }
              {
                item.evaluateList?.length > 0 && <>
                  <Divider orientation="left">评价</Divider>
                  <List
                    itemLayout="horizontal"
                    dataSource={item.evaluateList}
                    renderItem={(evaluateItem, index) => (
                      <List.Item>
                        <List.Item.Meta
                          avatar={<Avatar src={evaluateItem?.user?.userAvatar ? evaluateItem?.user?.userAvatar : 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg'} />}
                          title={evaluateItem?.user?.userAccount ? evaluateItem?.user?.userAccount : '用户已被注销'}
                          description={evaluateItem.content}
                        />
                      </List.Item>
                    )}
                  />
                </>
              }
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};
export default MyChartPage;
