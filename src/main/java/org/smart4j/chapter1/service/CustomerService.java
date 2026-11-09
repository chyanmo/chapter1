package org.smart4j.chapter1.service;

import org.apache.commons.lang3.StringUtils;
import org.smart4j.chapter1.helper.DatabaseHelper;
import org.smart4j.chapter1.model.Customer;

import java.util.List;
import java.util.Map;

public class CustomerService {
    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList() {
        String sql = "SELECT * FROM customer";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
    }

    /**
     * 获取客户列表，支持按姓名模糊搜索
     */
    public List<Customer> getCustomerList(String keyword){
        if (StringUtils.isBlank(keyword)) {
            return DatabaseHelper.queryEntityList(
                    Customer.class, "SELECT * FROM customer");
        }
        return DatabaseHelper.queryEntityList(
                Customer.class,
                "SELECT * FROM customer WHERE name LIKE ?",
                "%" + keyword.trim() + "%");
    }

    /**
     * 按 id 查询客户
     */
    public Customer getCustomer(long id){
        return DatabaseHelper.queryEntity(
                Customer.class, "SELECT * FROM customer WHERE id=?", id);
    }

    /**
     * 创建客户
     */
    public boolean createCustomer(Map<String, Object> fieldMap){
        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }

    /**
     * 更新客户
     */
    public boolean updateCustomer(long id, Map<String, Object> fieldMap){
        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }

    /**
     * 删除客户
     */
    public boolean deleteCustomer(long id){
        return DatabaseHelper.deleteEntity(Customer.class, id);
    }
}
