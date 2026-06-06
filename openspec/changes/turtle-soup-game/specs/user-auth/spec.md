## ADDED Requirements

### Requirement: 用户注册
系统 SHALL 允许新用户通过用户名和密码注册账号。

#### Scenario: 成功注册
- **WHEN** 用户提供未注册的用户名和符合要求的密码（至少6位）
- **THEN** 系统创建新账号，密码使用 bcrypt 加密存储，返回注册成功及 JWT Token

#### Scenario: 用户名已存在
- **WHEN** 用户提供的用户名已被注册
- **THEN** 系统返回错误提示"用户名已存在"

#### Scenario: 密码过短
- **WHEN** 用户提供的密码少于6位
- **THEN** 系统返回错误提示"密码长度至少为6位"

### Requirement: 用户登录
系统 SHALL 允许已注册用户通过用户名和密码登录。

#### Scenario: 成功登录
- **WHEN** 用户提供正确的用户名和密码
- **THEN** 系统验证通过，返回 JWT Token 和用户基本信息

#### Scenario: 密码错误
- **WHEN** 用户提供错误的密码
- **THEN** 系统返回错误提示"用户名或密码错误"

#### Scenario: 用户不存在
- **WHEN** 用户名不存在于系统中
- **THEN** 系统返回错误提示"用户名或密码错误"（不暴露用户存在性）

### Requirement: 获取个人信息
系统 SHALL 允许已登录用户获取自己的个人信息。

#### Scenario: 获取成功
- **WHEN** 已登录用户请求个人信息
- **THEN** 系统返回用户 ID、用户名、昵称、创建时间

### Requirement: 更新个人信息
系统 SHALL 允许已登录用户更新自己的昵称。

#### Scenario: 更新昵称
- **WHEN** 已登录用户提交新的昵称
- **THEN** 系统更新昵称并返回成功
