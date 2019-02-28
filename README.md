丁牛教师版 Dingniu Teacher

## 功能性需求 Functional Requirements

### 已完成 Finished

- 允许教师**登陆**、**注册**、**找回密码**
- 教师注册后可以填写资料并提交至服务器审核
- 教师个人资料包括**基本资料**、**教学资料**、**学历认证**、**教学经历**
- 教师登陆后可以查看最近的课程列表
- 教师登陆后可以查看过往的课程列表
- 教师可以查看并修改**一对一开课时间**
- 教师可以修改个人资料，此时除上文提及的资料外，还增加**荣誉认证**
- 教师可以修改提现方式并**提现**
- 我的课表，待上的课，点击一对一课程进入设置课程页面

### 未完成 Developing

- 消息页面，允许教师与学生**即时聊天**
- 我的收入页面，查看**收入明细**与**提现明细**
- 我的收入页面，结算说明
- 课程设置页面，查看修改一对一价格，以及其他几个静态页面
- 基本设置页面
- 登出
- 设置课程页面，设置课程内容

## 非功能性需求 Unfuncational Requirements

### 已完成 Finished

- 课程列表下拉刷新
- 一对一课程列表同时选中一行或一列
- 一对一课程列表点击效果
- 部分页面的沉浸式状态栏

--------------------------------------

## 项目结构说明 Project Structure
```
├── adapters
│   ├── MyClassAdapter.java 课程列表的Adapter，已上的课和待上的课都依赖此类
│   ├── SchedulePageAdapter.java 一对一课程列表的Adapter
│   └── TimetablePageAdapter.java 主界面课表的Adapter，用于加载已上的课和待上的课两个片段 
├── api
│   ├── CachingContrilInterceptor.java 用于在网络请求中写入请求头
│   ├── CourseQueryListApi.java 网络接口，获取教师课程列表
│   ├── ListLevelGradeSubjectApi.java 网络接口，获取科目、年级、阶段数据
│   ├── LoginApi.java 网络接口，登陆
│   ├── NormalAPi.java 网络接口，通用，大部分的请求调用此接口即可
│   ├── UploadFreeTimeApi.java 网络接口，上传一对一课程时间
│   ├── UploadImageApi.java 网络接口，上传图片
│   └── UploadInfoApi.java 网络接口， 上传个人信息
├── constant
│   ├── Constant.java 保存了一些常用常量
│   └── MethodCode.java 保存了各种网络请求的MethodCode常量
├── http
│   └── ApiInterface.java 网络请求接口
├── models
│   └── UserLogin
│   │   └── UserInfo.java 用户信息
│   ├── CourseBean.java 课程，用在一对一课程中
│   ├── ProvinceBean.java 省份信息，保存了中国省市区三级行政区信息，用在所在地区三级选择框
│   ├── Result.java 结果，保存网络请求结果
│   ├── TeacherInfo.java 教师信息
│   └── WeekBean.java 周，用在一对一课程中
├── ui
│   └── activities
│   │   └── my
│   │   │   ├── income
│   │   │   │   └── WithdrawActivity.java 提现页面，提现到支付宝和提现到银行卡都依赖此类
│   │   │   └── personalInfo
│   │   │   │   ├── MyBasicInfoActivity.java 基本资料
│   │   │   │   ├── MyDiplomaIdentification.java 学历认证
│   │   │   │   ├── MyEducationInfoActivity.java 教学资料
│   │   │   │   ├── MyHonorActivity.java 填写荣誉
│   │   │   │   ├── MyTeachingDemostration.java 教学展示
│   │   │   │   ├── MyTeachingExperiencesActivity.java 教学经历
│   │   │   │   └── MyWriteHonorActivity.java 荣誉认证
│   │   │   ├── BasicSettingActivity.java 基本设置页面
│   │   │   ├── MyIncomeActivity.java 我的收入页面
│   │   │   └── MyPersonalInfoActivity.java 个人资料页面
│   │   ├── CourseSettingActivity.java 课程设置
│   │   ├── DiaplomaIndentifyActivity.java 注册信息3-学历认证
│   │   ├── EducationInfoActivity.java 注册信息2-教学资料
│   │   ├── LoginActivity.java 登陆页面
│   │   ├── MainActivity.java 主页面
│   │   ├── ModifyPassword.java 修改密码页面
│   │   ├── PersonalActivity.java 注册信息1-个人信息
│   │   ├── ResisterActivity.java 注册页面
│   │   ├── ReviewStatusActivity.java 注册成功待审核页面
│   │   ├── ScheduleActivity.java 一对一开课时间页面
│   │   ├── SetLessonActivity.java 设置课程页面
│   │   └── StartActivity.java 起始页
│   ├── dialogs
│   │   └── IncomeWithdrawDialog.java 提现对话框，已弃用
│   └── fragments
│       ├── FinishedSubFragment.java 已上的课子片段
│       ├── MessageFragment.java 消息片段
│       ├── MyFragment.java 我的片段
│       ├── SubScheduleFragment.java 一对一开课时间片段，每周一个片段
│       ├── TimetableFragment.java 课程片段
│       └── UnfinishedSubFragment.java 待上的课子片段
├── utils
│   ├── BankCardTextWatcher.java 银行卡输入框，实现 0000 0000 0000 0000 000 这种效果
│   ├── OSUtils.java 判断ROM类型
│   ├── ScreenUtil.java 获得屏幕相关的辅助类
│   ├── SharedPreferencesHelper.java 保存信息配置类，省去了手动写SharedPreferences的麻烦
│   ├── StatusBarUtil.java 设置沉浸式状态栏
│   └── SystemBarTintManager.java 状态栏工具类
├── view
│   ├── AvatarBottomSheetDialog.java 头像底部弹框，选择拍照或相册
│   ├── DiplomaBottomSheetDialog.java 选择学历底部弹框
│   ├── EducationGradeBottomSheetDialog.java 选择教学年纪底部弹框
│   └── MobileEditText.java 手机号输入框，实现186 8888 6666这种效果
└── App.java Application类
```

-------------------------------------------------
## 网络请求列表 Api List
> 详情请见 com.xuetai.teacher.xuetaiteacher.constant.MethodCode.java

### 登录 Login

```json
{
    "method": "Teacher/Entry.login",
    "params": {
        "phone": "13333333333",
        "password": "123456",
        "deviceToken": "deviceToken"
    },
    "id": 1
}
```

### 注册 Register

```json
{
    "method": "Teacher/Entry.register",
    "params": {
        "phone": "13333333333",
        "password": "123456",
        "code": "2035"
    },
    "id": 1
}
```

### 忘记密码 ForgetPassword

```json
{
	"method": "Teacher/Entry.forgetPassword",
	"params": {
        "phone": "13333333333", 
        "password": "123456", 
        "code": "2035"
    },
	"id": 1
}
```

### 获取短信验证码 SmsCode

```json
{
	"method": "Common/Message.sendSmsCode",
	"params": {
		"mobile": "13333333333",
		"type": "type" //0-注册，1-修改密码
	},
	"id": 1
}
```

### 上传图片 UPLOADIMAGE

```json
{
    "method": "Common/File.uploadEncoded",
    "params": {
        "content": "base64",
        "extension": "png",
        "sub": "teacher_info"
    },
    "id": 1
}
```

### 更新信息 UpDateInfo

```json
{
      "method": "Teacher/Info.update",
      "params": {
            "examine_status": 3, //审核状态
            "phone": "13333333333", //电话号
            "head_photo": "", //头像图片网址
            "location": "", //所在地区
            "subject": "2", //教学科目
            "idcard_photo": "", //身份证图片地址
            "education": "2", // 
            "exp": [], //教学经历
            "cert": [], //学历证明
            "school": "测试学校", //学校
            "idcardno": "230103199999999999", //身份证号
            "level": "1", //教学阶段，初中or高中
            "nick": "测试", //昵称
            "real_name": "测试", //真实姓名
            "grade": "[1,2,3]", //教学年级
            "gender": "0", //性别
            "education_photo": "http://39.107.70.183:8008/upload/teacher_info/a6c3e49d0830e4513fe8725bbdb5739b.png", //学历证明照片网址
            "schooltime": "2011-2015", //在校时间
            "note": "测试老师" //备注
        },
      "id": 1
}
```

### 获取阶段年级科目信息 ListLevelGradeSubject

```json
{
    "method": "Common/Config.listLevelGradeSubject",
    "params": {},
    "id": 1
}
```

### 查询老师审核状态 ExamineStatus

```json
{
    "method": "Teacher/Info.examineStatus",
    "params": {},
    "id": 1
}

//返回的数据中：
//0-资料不完整
//1-资料完整没有审核
//2-资料完整审核不通过
//3-资料完整审核通过
```

### 初始化老师收入信息 TeacherIncome

```json
{
	"method": "Teacher/Info.balance",
	"params": {},
	"id": 1
}
//返回值balance是余额，incomeSum是累计收入
```

### 收入明细 IncomeDetail 

```json
{
	"method": "Teacher/Info.incomeList",
	"params": {
		"ceil": "201901" //此参数如果不传，则相当于当前的年月，如果传了201810，则获取2018年10月之前的20条左右的收入记录，如果第21~22条记录和第20条记录在同一个月，那就返回22条
	},
	"id": 1
}
```

### 获取老师一对一课表时间详情 TeacherInfoCalendar

```json
{
	"method": "Teacher/Time.timeList",
	"params": {},
	"id": 1
}

//返回一个jsonArray，存储着所有一对一课程
```

### 提现 WithdrawApply

```json
{
    "method": "Teacher/DrawApply.addApply",
    "params": {
        "money": "100" //提现金额
    },
    "id": 1
}
```

### 增加提现账号 AddTeacherAccountNum

```json
{
    "method": "Teacher/Info.updatePayAccount",
    "params": {
        "account": "12344567890123456789"
        "type": "0" //提现方式，0-支付宝，1-银行卡
    },
    "id":1
}
```

### 课程详细信息 CourseDetailInitial

```json
{
    "method": "Teacher/Course.detail",
    "params": {
        "id": "1650" //课程ID
    },
    "id": 1
}
```

### 修改教师一对一课表时间 UpdateTeacherFreeTime

```json
{
    "method": "Teacher/Time.updateFreeTime",
    "params": {
        //这里传一个JSONArray，格式类似于["2019-01-01 19:00", "2019-01-01 19:00"]
    },s
    "id": 1
}
```

### 登出 Logout

```json
{
    "method": "Teacher/Entry.logout",
    "params": {
        "phone": "13344446666"
    },
    "id": 1
}
```

### 获取教科书版本列表 TextBookVersionList

```json
{
    "method": "Teacher/Question.textbookVersionList",
    "params": {},
    "id": 1
}

//返回值
{
  "jsonrpc": "2.0",
  "result": [
    {
      "type": "depart",
      "title": "中考总复习",
      "content": [
        "统一知识点"
      ]
    },
    {
      "type": "textbook",
      "title": "初中同步",
      "content": [
        {
          "id": 285,
          "name": "北师大新版"
        },
        {
          "id": 286,
          "name": "人教新版"
        },
        {
          "id": 287,
          "name": "沪教新版"
        },
        {
          "id": 288,
          "name": "沪科新版"
        },
        {
          "id": 289,
          "name": "华师大新版"
        }
        //省略一些...
      ]
    }
  ],
  "id": 1,
  "code": 0
}
```

### 获取总复习内容列表 QuestionPointList

```json
{
    "method": "Teacher/Question.pointList",
    "params": {
        "id": "12345" //课程的id
    },
    "id": 1
}

//返回内容是树状图，每个内容类似于
{
	"id": 10040,
	"ordernum": 10040,
	"head": "有理数的加法",
	"exams": [],
	"checked": true,  //当checked为true，则exams无子项
}

```



-------------------------------------------------------------

## 第三方库依赖列表 List of Dependencies

