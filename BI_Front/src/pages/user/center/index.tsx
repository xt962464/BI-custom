import {LoadingOutlined, PlusOutlined} from '@ant-design/icons';
import React, {useState} from 'react';
import {
  Button, Card, Col, Divider,
  Form, message, Row,
  Select,
  Space, Spin, Typography,
  Upload, UploadFile,
} from 'antd';

import TextArea from "antd/es/input/TextArea";
import Input from "antd/es/input/Input";
import style from "./index.less"

import type { UploadProps } from 'antd/es/upload/interface';
import {UploadChangeParam} from "antd/lib/upload";
import {updateUserUsingPOST} from "@/services/BI_Front/userController";
import {useModel} from "@@/plugin-model/useModel";
import {getInitialState} from "@/app";

const AddChart: React.FC = () => {

  const [submitting, setSubmitting] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);
  const [imageUrl, setImageUrl] = useState<string>();
  const { initialState } = useModel('@@initialState');

  const currentUser = initialState?.currentUser;
  console.log('currentUser', initialState)

  const [form] = Form.useForm();
  form.setFieldsValue(currentUser);
  // setImageUrl(currentUser?.userAvatar);

  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);
    console.log('values', values)
    if (imageUrl) {
      values['userAvatar'] = imageUrl;
    }
    values['id'] = currentUser?.id;
    try {
      const res = await  updateUserUsingPOST(values)
      if (!res.data) {
        message.error('系统发生错误');
      } else {
        getInitialState();
        message.success('保存成功');
      }
    } catch (e: any) {
      message.error('保存失败：' + e.message);
    }
    setSubmitting(false);
  };

  const handleChange: UploadProps['onChange'] = (info: UploadChangeParam<UploadFile>) => {
    if (info.file.status === 'uploading') {
      setLoading(true);
      return;
    }
    if (info.file.status === 'done') {
        setLoading(false);
        setImageUrl(`http://127.0.0.1:8101/api${info.file.response.data}`);
    }
  };

  const uploadButton = (
    <div>
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>Upload</div>
    </div>
  );

  return (
    <div className="add-chart">
      <Row gutter={24}>
        <Col span={24}>
          <Card title="个人中心">
            <Form name="addChartRequest"
                  form={form}
                  onFinish={onFinish}
                  initialValues={{ }}
                  labelAlign="left"
                  labelCol={{span: 4}}
                  wrapperCol={{span: 10}}
            >
              <Form.Item name="userName" label="用户昵称">
                <Input placeholder="请输入用户昵称"  maxLength={10}></Input>
              </Form.Item>

              <Form.Item name="userProfile" label="用户简介">
                <TextArea placeholder="请输入用户简介"></TextArea>
              </Form.Item>

              <Form.Item
                name="userAvatarFile"
                label="头像"
                valuePropName="fileList"
                getValueFromEvent={e => {
                  if (Array.isArray(e)) {
                    return e;
                  }
                  return e && e.fileList;
                }}
              >
                <Upload
                  name="file"
                  listType="picture-card"
                  className="avatar-uploader"
                  showUploadList={false}
                  maxCount={1}
                  action="/api/file/upload-local"
                  onChange={handleChange}
                  style={{'border-radius': '50%'}}
                >
                  {imageUrl||currentUser?.userAvatar ? <img src={imageUrl||currentUser?.userAvatar} alt="avatar" style={{ width: '100%', 'height': '100%', 'border-radius' : '50%' }} /> : uploadButton}
                </Upload>
              </Form.Item>

              <Form.Item wrapperCol={{ span: 12, offset: 4 }}>
                <Space>
                  <Button type="primary" htmlType="submit" loading={submitting} disabled={submitting}>
                    保存
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
};
export default AddChart;
