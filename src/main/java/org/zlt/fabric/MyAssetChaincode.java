package org.zlt.fabric;

import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.zlt.fabric.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能合约
 *
 * @author zlt
 * @version 1.0
 * @date 2022/2/8
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Contract(name = "mycc")
@Default
public class MyAssetChaincode implements ContractInterface {
    public  MyAssetChaincode() {}

    /**
     * 初始化3条记录
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void init(final Context ctx) {
        addUser(ctx, "1", "zlt",100D);
        addUser(ctx, "2", "admin",200D);
        addUser(ctx, "3", "guest",300D);
    }

    /**
     * 新增用户
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public User addUser(final Context ctx, final String userId, final String name, final double money) {
        ChaincodeStub stub = ctx.getStub();
        User user = new User(userId, name, money);
        String userJson = JSON.toJSONString(user);
        stub.putStringState(userId, userJson);
        return user;
    }

    /**
     * 查询某个用户
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public User getUser(final Context ctx, final String userId) {
        ChaincodeStub stub = ctx.getStub();
        String userJSON = stub.getStringState(userId);
        if (userJSON == null || userJSON.isEmpty()) {
            String errorMessage = String.format("User %s does not exist", userId);
            throw new ChaincodeException(errorMessage);
        }
        User user = JSON.parseObject(userJSON, User.class);
        return user;
    }

    /**
     * 查询所有用户
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryAll(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<User> userList = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        for (KeyValue result: results) {
            User user = JSON.parseObject(result.getStringValue(), User.class);
            System.out.println(user);
            userList.add(user);
        }
        return JSON.toJSONString(userList);
    }

    /**
     * 转账
     * @param sourceId 源用户id
     * @param targetId 目标用户id
     * @param money 金额
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void transfer(final Context ctx, final String sourceId, final String targetId, final double money) {
        ChaincodeStub stub = ctx.getStub();
        User sourceUser = getUser(ctx, sourceId);
        User targetUser = getUser(ctx, targetId);
        if (sourceUser.getMoney() < money) {
            String errorMessage = String.format("The balance of user %s is insufficient", sourceId);
            throw new ChaincodeException(errorMessage);
        }
        User newSourceUser = new User(sourceUser.getUserId(), sourceUser.getName(), sourceUser.getMoney() - money);
        User newTargetUser = new User(targetUser.getUserId(), targetUser.getName(), targetUser.getMoney() + money);
        stub.putStringState(sourceId, JSON.toJSONString(newSourceUser));
        stub.putStringState(targetId, JSON.toJSONString(newTargetUser));
    }
}
