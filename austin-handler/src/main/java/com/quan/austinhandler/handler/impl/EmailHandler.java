package com.quan.austinhandler.handler.impl;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.google.common.base.Throwables;
import com.quan.austincommon.constant.SendAccountConstant;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.dto.model.EmailContentModel;
import com.quan.austincommon.enums.ChannelType;
import com.quan.austinhandler.handler.BaseHandler;
import com.quan.austinhandler.handler.Handler;
import com.quan.austinsupport.domain.MessageTemplate;
import com.quan.austinsupport.utils.AccountUtils;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * date: 2022/11/22 下午 4:35
 *
 * @author Four
 */

@Component
@Slf4j
public class EmailHandler extends BaseHandler implements Handler {

    @Autowired
    private AccountUtils accountUtils;

    public EmailHandler() {
        this.channelCode = ChannelType.EMAIL.getCode();
        // TODO 限流相关设置
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {
        // 1. 获取taskInfo与邮箱发送内容
        EmailContentModel emailContentModel = (EmailContentModel) taskInfo.getContentModel();
        // 2. 获取账号消息
        MailAccount account = getAccountConfig(taskInfo.getSendAccount());
        // 3. 使用Hutool中的工具类发送邮件
        try {
            MailUtil.send(account, taskInfo.getReceiver(), emailContentModel.getTitle(),
                    emailContentModel.getContent(), true, null);
        } catch (Exception e) {
            log.error("EmailHandler#handler fail!{},params:{}", Throwables.getStackTraceAsString(e), taskInfo);
            return false;
        }
        return true;
    }

    /**
     * 获取账号信息合配置
     *
     * @return
     */
    private MailAccount getAccountConfig(Integer sendAccount) {
        MailAccount account = accountUtils.getAccount(sendAccount, SendAccountConstant.EMAIL_ACCOUNT_KEY, SendAccountConstant.EMAIL_ACCOUNT_PREFIX, MailAccount.class);
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            account.setAuth(account.isAuth()).setStarttlsEnable(account.isStarttlsEnable()).setSslEnable(account.isSslEnable()).setCustomProperty("mail.smtp.ssl.socketFactory", sf);
            account.setTimeout(25000).setConnectionTimeout(25000);
        } catch (Exception e) {
            log.error("EmailHandler#getAccount fail!{}", Throwables.getStackTraceAsString(e));
        }
        return account;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
