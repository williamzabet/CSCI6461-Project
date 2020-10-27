package csci6461Project;

public class Instructions{
	/*
	 * This class gets the code and the binary digits are seperated by the operation, gpr, ixr, i, and address. They then
	 * get passed to the BackEnd class. 
	 * Main Developer: Zabet, William B. 
	 * Reviewer: Ajayi, Oluwasegun E.
	 */
	
	protected short opcode; // 
	protected short gprcode; // 
	protected short ixrcode;   // These are the codes for each component of the instruction. 
	protected short icode;  // 
	protected short address;// 
	
	
	public Instructions(short ir) { // this is a function that gets the code for each component of the instruction that is decoded from the IR. 
		// (part of the decode step of the cycle!)
		/*
		 * For each part of the code, the value from the index register gets converted into a 16 digit binary piece of code
		 * It then gets subStringed to isolate the binary digits for each respective field, and then it gets converted back into a decimal
		 */
		short code = ir;
		
		String op = String.format("%16s", Integer.toBinaryString(0xFFFF & code)).replaceAll(" ", "0").substring(0,6);
		int op_decimal = Integer.parseInt(op,2);
		opcode = (short) op_decimal;
		
		String gpr = String.format("%16s", Integer.toBinaryString(0xFFFF & code)).replaceAll(" ", "0").substring(6,8);
		int gpr_decimal = Integer.parseInt(gpr,2);
		gprcode = (short) gpr_decimal;
		
		String ixr = String.format("%16s", Integer.toBinaryString(0xFFFF & code)).replaceAll(" ", "0").substring(8,10);
		int ixr_decimal = Integer.parseInt(ixr,2);
		ixrcode = (short) ixr_decimal;
		
		String i = String.format("%16s", Integer.toBinaryString(0xFFFF & code)).replaceAll(" ", "0").substring(10,11);
		int i_decimal = Integer.parseInt(i,2);
		icode = (short) i_decimal;
		
		String addy = String.format("%16s", Integer.toBinaryString(0xFFFF & code)).replaceAll(" ", "0").substring(11);
		int addy_decimal = Integer.parseInt(addy,2);
		address = (short) addy_decimal;
	}
}
