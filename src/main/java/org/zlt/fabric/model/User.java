package org.zlt.fabric.model;

import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * 帐户对象
 *
 * @author zlt
 * @version 1.0
 * @date 2022/2/8
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@DataType
public class User {
    @Property
    private final String userId;

    @Property
    private final String name;

    @Property
    private final double money;

    public User(final String userId, final String name, final double money) {
        this.userId = userId;
        this.name = name;
        this.money = money;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        User other = (User) obj;
        return Objects.deepEquals(
                new String[] {getUserId(), getName()},
                new String[] {other.getUserId(), other.getName()})
                &&
                Objects.deepEquals(
                        new double[] {getMoney()},
                        new double[] {other.getMoney()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getName(), getMoney());
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }
}
