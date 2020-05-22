1、entity-dataObject 层维护Java对象与数据库表之间的映射, 负责数据存储与service层的数据传输。
2、Service-model 核心领域对象，业务处理所需要的全量信息
3、controller-viewObject 供前端展示的信息，保证前端只接收到其所需要的字段即可，避免关键信息泄露。
