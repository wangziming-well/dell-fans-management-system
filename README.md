



## ipmiTool
项目通过ipmi与服务器通信，需要保证:
* 机器的idrac和当前电脑在同一个网段中
* 服务器开启了ipmi (服务器idrac => 网络 => IPMI)

项目依赖于 impitool:
* linux环境下需要安装配置impitool
* window环境下不需要，本项目内置了win版本的impitool

## Redfish RESTFul API
项目依赖Redfish API 获取服务器状态



