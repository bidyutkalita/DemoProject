package com.practice.udeyme;

import java.math.BigInteger;

public class ComplexCalculation {
	public static void main(String[] args) {
		ComplexCalculation r = new ComplexCalculation();
		System.out.println(r.calculateResult(BigInteger.valueOf(2), BigInteger.valueOf(2),BigInteger.valueOf(2), BigInteger.valueOf(2)));
	}
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
        BigInteger result;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
        
        PowerCalculatingThread t1= new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread t2 = new PowerCalculatingThread(base2, power2);
        
        try {
        t1.join();
        t2.join();
        t1.start();
        t2.start();
        
        Thread.sleep(1000);
        }catch (Exception e) {
			// TODO: handle exception
		}
        return result=t1.getResult().add(t2.getResult());
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;
    
        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }
    
        @Override
        public void run() {
        	
        	 while (power.signum() > 0) {
        		    if (power.testBit(0)) result = result.multiply(base);
        		    base = base.multiply(base);
        		    power = power.shiftRight(1);
        		  }
        	//result=BigInteger.valueOf(Long.parseLong((Math.pow(Double.parseDouble(base+""),  Double.parseDouble(power+""))+"")));
        }
    
        public BigInteger getResult() { return result; }
    }
}