package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

public interface MediaFileProcessService {
    //查询未处理文件
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    //开启一个任务
    public boolean startTask(long id);

    //处理完成 保存任务结果
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
