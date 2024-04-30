import VirtualList from 'rc-virtual-list';
import {useEffect, useState} from 'react';
import {Button, Comment, Divider, List, message, Space} from "antd";
import {addCommentPOST, listCommentByPageUsingPOST} from "@/services/BI_Front/postCommentController";
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

let ContainerHeight = 500;
let currentPageNumber = 1;
let totalPageCount = 1;

export default () => {
  const [data, setData] = useState<API.Comment[]>([]);
  const [commentText, setCommentText] = useState<string>();

  /**
   * 加载留言列表
   */
  const loadCommentDataList = () => {
    const params: any = {
      current: currentPageNumber
    }
    listCommentByPageUsingPOST(params).then((resp) => {
      console.log('resp', resp)
      if (resp.code == 0) {
        // eslint-disable-next-line @typescript-eslint/no-shadow
        // @ts-ignore
        const respData: API.PageComment_ = resp.data;
        currentPageNumber = respData.current || 1;
        totalPageCount = respData.pages || 1;
        // 收集旧列表数据
        let oldDataList: API.Comment[] = JSON.parse(JSON.stringify(data))
        // 合并新数据
        oldDataList = oldDataList.concat(respData.records || []);
        setData(oldDataList);
      }
    })
  };

  /**
   * 发布留言
   */
  const sendComment = async () => {
    console.log('发布留言', commentText)
    if (commentText) {
      const params: API.CommentAddRequest = {
        content: commentText
      }
      const resp = await addCommentPOST(params);
      if (resp.code == 0) {
        message.success("发布成功");
        // 清空输入框
        setCommentText('');
        // 重新加载
        currentPageNumber = 1
        loadCommentDataList();
      } else {
        message.error("发布失败")
      }
    }
  }

  /**
   * 输入框输入事件
   * @param e
   */
  const commentTextChange = (e: any) => {
    setCommentText(e.target.value);
  }

  useEffect(() => {
    const main = document.getElementsByClassName('ant-layout-content');
    console.log('ContainerHeight', ContainerHeight)
    if (main.length > 0) {
      ContainerHeight = main[0]?.offsetHeight - 150;
    }
    console.log('ContainerHeight', ContainerHeight)
    loadCommentDataList();
  }, []);

  const onScroll = (e: React.UIEvent<HTMLElement, UIEvent>) => {
    if (e.currentTarget.scrollHeight - e.currentTarget.scrollTop === ContainerHeight) {
      if (currentPageNumber < totalPageCount) {
        currentPageNumber++;
        loadCommentDataList();
      }
    }
  };

  return (
    <>
      <List key="comment-list">
        <VirtualList
          key="comment-virtua-list"
          data={data}
          height={ContainerHeight}
          itemKey="email"
          onScroll={onScroll}
        >
          {(item: API.Comment) => (
            <li>
              <Comment
                author={item?.user?.userAccount ? item?.user?.userAccount : '用户已被注销'}
                avatar={item?.user?.userAvatar ? item?.user?.userAvatar : 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg'}
                content={item.content}
                datetime={item.createTime}
              />
              <Divider/>
            </li>
          )}
        </VirtualList>
      </List>
      <Space key="comment-send-input" direction="vertical" style={{"width": "100%"}}>
        <Space.Compact key="comment-space"
                       style={{width: '100%', display: 'flex', 'alignItems': 'center', 'justifyContent': 'center'}}>
          <TextArea value={commentText} autoSize={false} showCount maxLength={250} onChange={commentTextChange}
                    key="commen-inpt" placeholder="请输入内容" autoSize={{minRows: 6, maxRows: 10}}
                    style={{"height": "100px", "width": "100%"}}/>
          <Button key="comment-submit-btn" type="primary" style={{'marginLeft': '10px'}}
                  onClick={sendComment}>发布留言</Button>
        </Space.Compact>
      </Space>
    </>
  );
};
