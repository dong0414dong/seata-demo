package cn.itcast.storage.service.impl;

import cn.itcast.storage.entity.StorageFreeze;
import cn.itcast.storage.mapper.StorageFreezeMapper;
import cn.itcast.storage.mapper.StorageMapper;
import cn.itcast.storage.service.StorageTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：dongdong
 * @date ：Created in 2022/3/25 0025 21:53
 * @description： 这是描述
 * @modified By：
 */

@Service
public class StorageTCCServiceImpl implements StorageTCCService {

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private StorageFreezeMapper storageFreezeMapper;

    @Override
    public void deduct(String commodityCode, int count) {

        String xid = RootContext.getXID();

        ///业务悬挂问题,如果 cancel 已经执行执行,那么就不需要再执行 try业务
        //怎么实现?  就是判断冻结表里面是否已经存在这么一条 冻结数据,如果存在说明cancel已经执行过了
        StorageFreeze storageFreeze1 = storageFreezeMapper.selectById(xid);
        if (storageFreeze1 != null) {
            return;
        }
        //try阶段
        storageMapper.deduct(commodityCode, count);
        //检测预留资源
        StorageFreeze storageFreeze = new StorageFreeze();
        storageFreeze.setCommodityCode(commodityCode);
        storageFreeze.setState(StorageFreeze.State.TRY);
        storageFreeze.setXid(xid);
        storageFreezeMapper.insert(storageFreeze);

    }

    @Override
    public boolean confirm(BusinessActionContext ctx) {
        //confirm  需要删除预留的资源
        String xid = ctx.getXid();
        int count = storageFreezeMapper.deleteById(xid);
        return count == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {

        //判断是否存在预留资源
        StorageFreeze storageFreeze = storageFreezeMapper.selectById(ctx.getXid());
        String commodityCode = ctx.getActionContext("commodityCode").toString();
        if (storageFreeze == null) {
            // 空回滚
            StorageFreeze cancelStorage = new StorageFreeze();
            cancelStorage.setFreezeCount(0);
            cancelStorage.setState(StorageFreeze.State.CANCEL);
            cancelStorage.setXid(ctx.getXid());
            cancelStorage.setCommodityCode(commodityCode);
            storageFreezeMapper.insert(cancelStorage);
        }

        //幂等
        if (storageFreeze.getState().equals(StorageFreeze.State.CANCEL)) {
            //已经处理过
            return true;

        }
        storageMapper.refund(storageFreeze.getCommodityCode(), storageFreeze.getFreezeCount());

        //把预留资源的状态修改为取消
        storageFreeze.setState(StorageFreeze.State.CANCEL);
        storageFreeze.setFreezeCount(0);
        int count = storageFreezeMapper.updateById(storageFreeze);
        return count == 1;

    }
}
