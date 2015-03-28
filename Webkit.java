package MLB;

import org.json.simple.*;
public class Webkit 
{
    JSONObject accExist;
    JSONObject pinStatus;
    JSONObject balanceAmount;
    JSONObject withdrawStatus;
    JSONObject wdAmount;
    JSONObject receiptStatus;
    public void sendAccExist(int accountExist)
    {
        if (accExist != null) // Als het object niet leeg is, leegmaken om hem opnieuw te gebruiken.
        {
            accExist = null;
        }
        accExist = new JSONObject();
        accExist.put("accountExist", accountExist);
        System.out.println(accExist); // dit moet verzonden worden naar de webkit
    }
    public void sendPinStatus(boolean pinVerified , String accountState)
    {
        if (pinStatus != null)
        {
            pinStatus = null;
        }
        pinStatus = new JSONObject();
        if ("LOCK".equals(accountState))
        {
            pinStatus.put("accLock", true);
            pinStatus.put("pinVerified", pinVerified);
        }
        else if ("OPEN".equals(accountState))
        {
            pinStatus.put("accLock", false);
            pinStatus.put("pinVerified", pinVerified);
        }
        System.out.println(pinStatus);
    }
    public void sendBalance(int balance)
    {   
        if (balanceAmount != null)
        {
            balanceAmount = null;
        }
        balanceAmount = new JSONObject();
        balanceAmount.put("pinVerified", balanceAmount);
        System.out.println(balanceAmount);
    }
    public void sendWithdrawError(boolean withdrawSuccess)
    {
        if (withdrawStatus != null)
        {
            withdrawStatus = null;
        }
        withdrawStatus = new JSONObject();
        withdrawStatus.put("withdrawsuccess", withdrawSuccess );
        System.out.println(withdrawStatus);
    }
    public void sendWithdrawAmount(int withdrawAmount)
    {
        if (wdAmount != null)
        {
            wdAmount = null;
        }
        wdAmount = new JSONObject();
        wdAmount.put("withdrawAmount", withdrawAmount);
        System.out.println(wdAmount);
    }
    public void sendReceiptStatus(boolean receiptRequested)
    {
        if (receiptStatus != null)
        {
            receiptStatus = null;
        }
        receiptStatus = new JSONObject();
        if (receiptRequested == true)
        {
            receiptStatus.put("receiptRequested", true);
        }
        else if(receiptRequested == false)
        {
            receiptStatus.put("receiptRequested", true);
        }
    }        
}
