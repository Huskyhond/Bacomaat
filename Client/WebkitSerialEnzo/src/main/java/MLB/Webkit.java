package MLB;

import org.json.simple.*;

public class Webkit 
{
    JSONObject accExist;
    JSONObject pinStatus;
    JSONObject accStatus;
    JSONObject failCountObj;
    JSONObject balanceAmount;
    JSONObject withdrawStatus;
    JSONObject wdAmount;
	JSONObject wdDigit;
    JSONArray stacksOptionsArray;
    JSONObject stacksOptions;
    JSONObject receiptStatus;
    JSONObject cancelReq;
    JSONObject clearPinInput;
	JSONObject clearWDInput;
    JSONObject pinLengthObj;
    JSONObject backRequest;
    JSONObject withdrawRequest;
    
    WebkitConnect objSender;
    
    public Webkit() 
    {
        objSender =  new WebkitConnect();
    }
    
    public void sendAccExist(boolean accountExist)
    {
        accExist = new JSONObject();
        if (accountExist)
        {
            accExist.put("page", "code");
        }
        else 
        {
            accExist.put("page", "finish");
            accExist.put("failed", 1);
            accExist.put("message", "fak jou je account bestaat niet");
        }
        objSender.sendObject(accExist);
    }
    public void sendAccStatus(boolean accountStatus)
    {
        accStatus = new JSONObject();
        if (accountStatus)
        {
            accStatus.put("status", true);
        }
        else
        {
            accStatus.put("status", false);
        }
        objSender.sendObject(accStatus);
    }
    public void sendPinLength(String pinLength)
    {
        int codeLength = Integer.parseInt(pinLength.replaceAll("[^\\d.]", ""));
        pinLengthObj = new JSONObject();
        pinLengthObj.put("page", "code");
        pinLengthObj.put("codelength", codeLength);
        objSender.sendObject(pinLengthObj);
}
    public void sendPinStatus(boolean pinVerified)
    {
        pinStatus = new JSONObject();
        if (pinVerified)
        {
            pinStatus.put("page", "select");
        }
        else
        {
            
        }
        objSender.sendObject(pinStatus);
    }
    public void sendFailCount(int failcount)
    {
        failCountObj = new JSONObject();
        failCountObj.put("page", "code");
        failCountObj.put("failCount", failcount);
        objSender.sendObject(failCountObj);
            
    }
    public void sendBalance(int balance)
    {   
        balanceAmount = new JSONObject();
        balanceAmount.put("page", "balance");
        balanceAmount.put("amount", balance);
        objSender.sendObject(balanceAmount);
    }
    public void sendWithdrawError()
    {
        if (withdrawStatus != null)
        {
            withdrawStatus = null;
        }
        withdrawStatus = new JSONObject();

        withdrawStatus.put("page", "money");
        withdrawStatus.put("message", "Niet genoeg saldo");
        System.out.println(withdrawStatus.toJSONString());
        objSender.sendObject(withdrawStatus);
    }
    public void sendWithdrawAmount(String withdrawAmount)
    {
        wdAmount = new JSONObject();
        wdAmount.put("page", "receipt");
        wdAmount.put("amount", withdrawAmount);
        objSender.sendObject(wdAmount);
    }
    public void sendWithdrawDigit(String withdrawDigit)
    {
		wdDigit = new JSONObject();
		wdDigit.put("page", "money");
		wdDigit.put("digit", withdrawDigit);
		objSender.sendObject(wdDigit);
    }
    public void sendMoneyOptions(int[] moneyOptions)
    {
        stacksOptionsArray = new JSONArray();
        stacksOptions = new JSONObject();
        stacksOptionsArray.add(moneyOptions[0]);
        stacksOptionsArray.add(moneyOptions[1]);
        stacksOptionsArray.add(moneyOptions[2]);
        stacksOptionsArray.add(moneyOptions[3]);
        stacksOptionsArray.add(moneyOptions[4]);
        stacksOptions.put("moneyOptions", stacksOptionsArray);
        objSender.sendObjectArray(stacksOptions);
    }
    public void sendReceiptStatus(boolean receiptRequested)
    {
        receiptStatus = new JSONObject();
        if (receiptRequested == true)
        {
            receiptStatus.put("page", "finish");
            receiptStatus.put("message", "Uw bon wordt geprint");
            receiptStatus.put("receiptRequested", true);
        }
        else if(receiptRequested == false)
        {
            receiptStatus.put("page", "finish");
            receiptStatus.put("message", "Fijne dag verder");
            receiptStatus.put("receiptRequested", false);
        }
        objSender.sendObject(receiptStatus);
    }
    public void sendCancelRequest()
    {
        cancelReq = new JSONObject();
        cancelReq.put("page" , "finish");
        cancelReq.put("message", "Bedankt en tot ziens!");
        objSender.sendObject(cancelReq);
    }
    public void sendClearPinInput()
    {
        clearPinInput = new JSONObject();
<<<<<<< HEAD
        //clearPinInput.put("page", "code");
        clearPinInput.put("codelength", 0);
        objSender.clearMoney();
=======
        clearPinInput.put("page", "code");
        clearPinInput.put("codelength", 0);
>>>>>>> origin/master
        objSender.sendObject(clearPinInput);
    }
	public void sendClearWithdrawInput()
	{
		clearWDInput = new JSONObject();
		clearWDInput.put("page","withdraw");
<<<<<<< HEAD
		objSender.clearMoney();
=======
>>>>>>> origin/master
		objSender.sendObject(clearWDInput);
	}
    public void sendBackRequest()
    {
        backRequest = new JSONObject();
        backRequest.put("page", "select");
        objSender.sendObject(backRequest);
    }
    public void sendWithdrawRequest()
    {
        withdrawRequest = new JSONObject();
        withdrawRequest.put("page", "money");
        objSender.sendObject(withdrawRequest);
    }
}
