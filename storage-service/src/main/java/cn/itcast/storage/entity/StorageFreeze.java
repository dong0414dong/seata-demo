package cn.itcast.storage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 虎哥
 */
@Data
@TableName("storage_freeze_tbl")
public class StorageFreeze {
    @TableId(type = IdType.INPUT)
    private String xid;
    private String commodityCode;
    private Integer freezeCount;
    private Integer state;

    public static abstract class State {
        public final static int TRY = 0;
        public final static int CONFIRM = 1;
        public final static int CANCEL = 2;
    }
}
