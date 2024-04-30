export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './user/Login'},
      { name: '注册', path: '/user/register', component: './user/Register'},
      { component: './404' }
    ],
  },
  { name: '欢迎', path: '/welcome', icon: 'smile', component: './Welcome' },
  { name: '生成图表（同步）', path: '/add_chart', icon: 'barChart', component: './AddChart' },
  { name: '生成图表（异步）', path: '/add_chart_async', icon: 'barChart', component: './AddChartAsync' },
  { name: '我的图表', path: '/my_chart', icon: 'pieChart', component: './MyChart' },
  { name: '留言中心', path: '/comment', icon: 'Comment', component: './Comment' },
  { name: '个人中心', path: '/account/center', icon: 'User', component: './user/center' },
  {
    name: '管理页',
    path: '/admin',
    icon: 'crown',
    access: 'canAdmin',
    component: './Admin',
    routes: [
      { name: '用户管理', path: '/admin/userManager', icon: 'smile', component: './Admin/UserManager' },
      { name: '评价管理', path: '/admin/EvaluateManager', icon: 'smile', component: './Admin/EvaluateManager' },
      { name: '留言管理', path: '/admin/CommentManager', icon: 'smile', component: './Admin/CommentManager' },
      { component: './404' },
    ],
  },
  // { name: '查询表格', icon: 'table', path: '/list', component: './TableList' },
  { path: '/', redirect: '/welcome' },
  { component: './404' },
];
