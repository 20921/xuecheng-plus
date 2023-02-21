package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    //查询未处理文件
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    //开始任务
    @Override
    public boolean startTask(long id) {
//        int result = mediaProcessMapper.startTask(id);
        return mediaProcessMapper.startTask(id) <= 0 ? false : true;
    }

    /**
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     * @return void
     * @description 保存任务结果
     */
    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //查询是否有此任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            //如没有此任务 则直接停止程序运行
            return;
        }
        //如果执行因某种原因失败了
        if ("3".equals(status)) {
            //根据id更新数据库中的状态
            MediaProcess mediaProcessError = new MediaProcess();
            mediaProcessError.setStatus("3");
            //状态改完 3
            mediaProcessError.setFailCount(mediaProcess.getFailCount() + 1);
            // 失败次数+1
            mediaProcessError.setErrormsg(errorMsg);
            //写入报错原因
            mediaProcessMapper.update(mediaProcessError, new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId));
            log.error("改后缀失败信息为:{}", errorMsg);
        }
        //但凡成功了
        //先改url
        mediaProcess.setUrl(url);
        //拳打状态
        mediaProcess.setStatus("2");
        //脚踢完成时间
        mediaProcess.setFinishDate(LocalDateTime.now());
        //整改数据表
        mediaProcessMapper.updateById(mediaProcess);
        //过去的都将成为历史
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        //将荣耀写入历史手册
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //删除过去 尘封历史
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }
}
