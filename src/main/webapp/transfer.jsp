<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Account - JSP - JMS - Servlet</title>
</head>
<body>
<form action="TransferServlet" method="post">
    <fieldset>
        <legend>Transfer Money</legend>
            <lable for="originAccount">Origin Account: </lable>
            <input type="text" name="originAccount" id="originAccount">
            <lable for="destinationAccount">Destination Account: </lable>
            <input type="text" name="destinationAccount" id="destinationAccount">
            <lable for="amount">Amount: </lable>
            <input type="text" name="amount" id="amount">
            <input type="submit" value="Transfer">
    </fieldset>
</form>
<a href="CreateAccountServlet">Register?</a>
</body>
</html>