package cn.itcast.storage.service;

import org.springframework.stereotype.Service;


public interface StorageService{

    /**
     * 扣除存储数量
     */
    void deduct(String commodityCode, int count);
}