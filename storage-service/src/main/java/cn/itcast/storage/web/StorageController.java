package cn.itcast.storage.web;

import cn.itcast.storage.service.StorageService;
import cn.itcast.storage.service.StorageTCCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 虎哥
 */
@RestController
@RequestMapping("storage")
public class StorageController {

    @Autowired
    private  StorageService storageService;

    @Autowired
    private  StorageTCCService storageTCCService;

    /**
     * 扣减库存
     *
     * @param code  商品编号
     * @param count 要扣减的数量
     * @return 无
     */
    @PutMapping("/{code}/{count}")
    public ResponseEntity<Void> deduct(@PathVariable("code") String code, @PathVariable("count") Integer count) {
        //storageService.deduct(code, count);
        //tcc  模式接口实现
        storageTCCService.deduct(code, count);
        return ResponseEntity.noContent().build();
    }
}
