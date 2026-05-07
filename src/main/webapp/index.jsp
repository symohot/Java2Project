<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Account - JSP - JMS - Servlet</title>
</head>
<body>
<div><p>${messageC}</p></div>
<form action="CreateAccountServlet" method="post">
    <fieldset>
        <legend>Register Account In Bank Narmak</legend>
            <lable for="accountNumber">Account Number: </lable>
            <input type="text" name="accountNumber" id="accountNumber" placeholder="Must Start with 100">
            <lable for="firstName">First Name: </lable>
            <input type="text" name="firstName" id="firstName">
            <lable for="lastName">Last Name: </lable>
            <input type="text" name="lastName" id="lastName">
            <lable for="amount">Amount: </lable>
            <input type="text" name="amount" id="amount">
            <lable for="status">Status: </lable>
            <select name="status" id="status">
                <option value="OPEN">Open</option>
                <option value="CLOSE">Closed</option>
                <option value="BANNED">Banned</option>
            </select>
            <input type="submit" value="Register">
    </fieldset>
</form>
<a href="transfer.jsp">Transfer Money?</a>
</body>
</html>