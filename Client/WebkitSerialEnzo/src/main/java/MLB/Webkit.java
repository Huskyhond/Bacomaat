package MLB;

import org.json.simple.*;

public class Webkit 
{
    JSONObject accExist;
    JSONObject pinStatus;
	JSONObject accBlock;
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
    
    public void scanPas()
    {
    	JSONObject j = new JSONObject();
    	j.put("page","code");
    	objSender.sendObject(j);
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
            accExist.put("message", "Account bestaat niet of is geblokkeerd, probeert u het later nog eens of neem contact op met de bank.");
        }
        objSender.sendObject(accExist);
    }
	public void sendAccBlock(boolean accBlocked)
	{
		accBlock = new JSONObject();
		if (accBlocked)
		{
			accBlock.put("page", "finish");
			accBlock.put("message", "U heeft tevaak een foute pincode ingevoerd. Uw account is geblokkeerd. Neem contact op met de bank.");
		}
		else
		{
			
		}
		objSender.sendObject(accBlock);
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
        wdAmount.put("page", "confirm");
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
        cancelReq.put("message", "Bedankt en tot ziens");
        objSender.sendObject(cancelReq);
    }
    public void sendClearPinInput()
    {
        clearPinInput = new JSONObject();
        //clearPinInput.put("page", "code");
        clearPinInput.put("codelength", 0);
        objSender.clearMoney();
        objSender.sendObject(clearPinInput);
    }
	public void sendClearWithdrawInput()
	{
		clearWDInput = new JSONObject();
		clearWDInput.put("page","money");
		objSender.clearMoney();
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
    public void toReceipt()
    {
    	JSONObject j = new JSONObject();
    	j.put("page","receipt");
    	objSender.sendObject(j);
    }
    public void invalidSaldo()
    {
    	JSONObject s = new JSONObject();
    	s.put("page","confirm");
    	s.put("error",10);
    	s.put("message","Niet genoeg saldo.");
    	objSender.sendObject(s);

    }
}
