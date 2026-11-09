<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="BASE" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>客户管理</title>
    <style>
        body {
            margin: 32px;
            color: #222;
            font-family: Arial, "Microsoft YaHei", sans-serif;
        }

        table {
            width: 100%;
            max-width: 960px;
            border-collapse: collapse;
        }

        th,
        td {
            padding: 10px 12px;
            border: 1px solid #d8d8d8;
            text-align: left;
        }

        th {
            background: #f3f4f6;
        }

        a {
            margin-right: 12px;
            color: #075ea8;
        }

        .empty {
            padding: 24px 0;
            color: #666;
        }
    </style>
</head>
<body>
<h1>客户列表</h1>

<c:choose>
    <c:when test="${empty customerList}">
        <p class="empty">暂无客户数据</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
            <tr>
                <th>客户名称</th>
                <th>联系人</th>
                <th>电话号码</th>
                <th>邮箱地址</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="customer" items="${customerList}">
                <tr>
                    <td><c:out value="${customer.name}"/></td>
                    <td><c:out value="${customer.contact}"/></td>
                    <td><c:out value="${customer.telephone}"/></td>
                    <td><c:out value="${customer.email}"/></td>
                    <td>
                        <a href="${BASE}/customer_edit?id=${customer.id}">编辑</a>
                        <a href="${BASE}/customer_delete?id=${customer.id}">删除</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
