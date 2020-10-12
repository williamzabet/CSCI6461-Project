package csci6461Project;

public class Instructions{
	
	protected short opcode; // 
	protected short gprcode; // 
	protected short ixrcode;   // These are the codes for each component of the instruction. 
	protected short iValue;  // 
	protected short address;// 
	
	
	public Instructions(short ir) { // this is a function that gets the code for each component of the instruction that is decoded from the IR. 
		// (part of the decode step of the cycle!)
		opcode = getOperation(ir);
		gprcode = getgprcode(ir);
		ixrcode = getixrcode(ir);
		address = getAddress(ir);
	}
	
	// gets the opcode for the first binary digits in the instruction. 
	private short getOperation(short ir) {
		short op = ir;
		op = (short) (op >> 10); // using >> to shift the bits and isolate!
		return op;
	}
	
	// I need to find a better way to get the following values. These are very clunky/messy and do not really work. 
	// the gprcode gets returned correctly unless it = 3, it gets returned as 1 :(. however, 00, 01, 10 work. but not 11. 
	// What I tried to do was to get the values by isolating them but this doesnt seem to work well. I was thinking of getting the text value from the 
	// textfield of the IR but I have no clue how to do that / don't think it's possible as these functions are what make the textfields able to be updated 
	// in the first place. 
	private short getgprcode(short ir) {
		short gprcode = ir;
		gprcode = (short) (gprcode << 6);
		gprcode = (short) Math.abs((gprcode >> 14));
		return gprcode;
	}
	
	private short getixrcode(short ir) {
		short ixrcode = ir;
		ixrcode = (short) (ixrcode << 8);
		ixrcode = (short) Math.abs((ixrcode >> 14));
		return ixrcode;
	}
	
	private short getAddress(short ir) {
		short addy = ir;
		addy = (short) (addy << 11);
		addy = (short) Math.abs((addy >> 11));
		return addy;
	}
	

}
