package MLB;

import org.json.simple.*;

public class Webkit 
{
    JSONObject accExist;
    JSONObject pinStatus;
    JSONObject failCountObj;
    JSONObject balanceAmount;
    JSONObject withdrawStatus;
    JSONObject wdAmount;
    JSONArray stacksOptionsArray;
    JSONObject stacksOptions;
    JSONObject receiptStatus;
    JSONObject cancelReq;
    JSONObject clearInput;
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
        else if (!pinVerified)
        {
            pinStatus.put("page", "code");
            pinStatus.put("failed", 1);
        }
        objSender.sendObject(pinStatus);
    }
    public void sendFailCount(int failcount)
    {
        failCountObj = new JSONObject();
        failCountObj.put("kebab", failcount);
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
        withdrawStatus.put("message", "fak jou zoveel geld heb je nie");
        System.out.println(withdrawStatus.toJSONString());
        objSender.sendObject(withdrawStatus);
    }
    public void sendWithdrawAmount(String withdrawAmount)
    {
        wdAmount = new JSONObject();
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
            receiptStatus.put("message", "je bon wordt geprint");
            receiptStatus.put("receiptRequested", true);
        }
        else if(receiptRequested == false)
        {
            receiptStatus.put("page", "finish");
            receiptStatus.put("message", "doei");
            receiptStatus.put("receiptRequested", false);
        }
        objSender.sendObject(receiptStatus);
    }
    public void sendCancelRequest()
    {
        cancelReq = new JSONObject();
        cancelReq.put("page" , "finish");
        cancelReq.put("message", "geannuleerd doei");
        objSender.sendObject(cancelReq);
    }
    public void sendClearInput()
    {
        clearInput = new JSONObject();
        clearInput.put("page", "code");
        clearInput.put("codelength", 0);
        objSender.sendObject(clearInput);
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
        withdrawRequest.put("page", "withdraw");
        objSender.sendObject(withdrawRequest);
    }
}
