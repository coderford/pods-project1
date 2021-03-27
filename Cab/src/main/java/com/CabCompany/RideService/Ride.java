package com.CabCompany.Cab;

//import com.CabCompany.Cab.Cabstate;
//import com.CabCompany.Cab.Ridestate;
import java.lang.*;


public class Ride {
    private int custId;
    private int cabId;
    private int rideId=0;
    private Cabstate cabstate;
    private Ridestate ridestate;
    private int Pos;
    private int sourcLoc;
    private int destLoc;
    private int fare;
    private int signedIn;

    public Ride(int custId)
    {
        this.custId=custId;
        this.cabId=-1;
        this.rideId=-1;
        this.cabstate=cabstate.AVAILABLE;
        this.ridestate=ridestate.ENDED;
        this.Pos=0;
        this.sourcLoc=0;
        this.destLoc=0;
        this.fare=0;
        this.signedIn=-1;
    }

    public Ride(int cabId,int initialPos)
    {
        this.custId=0;
        this.cabId=cabId;
        this.rideId=-1;
        this.cabstate=Cabstate.AVAILABLE;
        this.ridestate=Ridestate.ENDED;
        this.Pos=initialPos;
        this.sourcLoc=0;
        this.destLoc=0;
        this.fare=0;
        this.signedIn=-1;
    }

    public int requestRide(int custId,int sourcLoc,int destLoc)
    {
        rideId++;
      //  if(cabServcie.requestRide(rideId) )
      {
        fare=10*(Math.abs(initialPos-sourcLoc)+Math.abs(sourcLoc-destLoc));
        //deduct fare from wallet

      }
      rideId--;
        return -1;
    }

    public boolean cabSignIn(int cabId, int initialPos)
    {
        if(cabstate==Cabstate.AVAILABLE && IsValidCabId(cabId))
        {
            this.signedIn=1;
            this.cabId=cabId;
            this.Pos=initialPos;
            return true;
        }
        return false;
    }


    public boolean cabSignOut(int cabId)
    {
        if(cabstate==Cabstate.AVAILABLE && IsValidCabId(cabId))
        {
            this.signedIn=-1;
            return true;
        }
        return false;
    }



    public String getCabStatus(int cabId)
    {
        if(ridestate==Ridestate.GIVING_RIDE)
        {
            return (ridestate+" "+sourcLoc+" "+custId+" "+destLoc);
        }
        else if(signedIn==1)return (ridestate+" "+Pos);

        return "-1";
    }

    public boolean IsValidCabId(int cabId)
    {
        
       return false;

    }
    
}
