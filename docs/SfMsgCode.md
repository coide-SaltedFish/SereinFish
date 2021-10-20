# SFMsgCode使用文档

## 基础格式

\<SF:[类型]:[参数]>

注：除特殊注明外，类型文本不区分大小写

## \<SF:Reply>

参数：无参数

范围：只能用于文本最开始

效果：回复触发消息

注意：此标签强匹配

## \<SF:Split:[可选参数]>

参数：下一条消息发送延时，单位毫秒

范围：文本任意部分

效果：分割消息

注意：此标签强匹配

## \<SF:sender:[参数]>

参数：`Name`、`Id`、`NameCard`、`HeadImage`、`At`

范围：文本任意部分

效果：填充触发者的信息

## \<SF:group:[参数]>

参数：`Name`、`Id`、`HeadImage`

生效区域：群消息

范围：文本任意部分

效果：填充群信息

## \<SF:time:[参数]>

参数：时间格式，如`yyyy-dd`

范围：文本任意部分

效果：按提交格式填充时间信息

## \<SF:random:[参数]>

参数：可选，默认为`0，100`,填写格式参考默认

范围：文本任意部分

效果：返回范围内随机数，包含边界

## 动态拓展标签（开发者使用）

1. 新建类并继承`SFMsgCode`接口

2. 使用`SFMsgCodeInfo`注解指定类型

### 示例：

```java
@SFMsgCodeInfo("sender")
public class Sender implements SFMsgCode {
    @Override
    public String error(Exception e) {
        throw new DoNone();
    }

    @Override
    public String code(SFMsgCodeContact codeContact) throws Exception {
        String para = codeContact.getParameter();

        if (para.equals("Name")){
            return codeContact.getSender().getName();
        }
    }
}
```

实现了在消息中包含`<SF:Sender:Name>`标签时替换为发送者名称