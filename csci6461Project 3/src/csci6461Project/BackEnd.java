package csci6461Project;

import java.io.BufferedReader; // reads in the program.txt file
import java.io.File; // 
import java.io.FileReader; // 
import java.io.IOException; // io exception for when file isnt found. 


public class BackEnd extends Thread {
	/*
	 * This class is the backend that declares each function which is then used to update the GUI
	 * when needed by the MainGUI, the words from the instruction codes are fetched, decoded, and excecuted, the codes
	 * are attained from the Instructions class
	 * Main Developer: Zabet, William B. 
	 * Reviewer: Ajayi, Oluwasegun E.
	 */
	
	public MainGUI gui;
	
	// all CPU registers that are displayed. 
	protected short gpr0 = 0;  
	protected short gpr1 = 0;  
	protected short gpr2 = 0; 
	protected short gpr3 = 0; 
	protected short ixr1 = 0;
	protected short ixr2 = 0;
	protected short ixr3 = 0;
	protected short pc = 0;
	protected short mar = 0;
	protected short mbr = 0;
	protected short ir = 0;
	protected short mfr = 0;
	protected short cc = 0;
	private boolean run = false;
	protected short[] memory = new short[2048]; // machine can have up to 2048 words maximum! 
	
	public BackEnd(MainGUI gui) { // updates the GUI when needed. 
		this.gui = gui;
		this.start();
		updateGUI();
	}
		
	
	public void runGUI(boolean state) {
		run = state;
	}
	
	// this updates the GUI and updates the textfields when a load button is pressed.
	// converts shorts to strings and then resizes the binary string based on byte size
	public void updateGUI() {
		gui.textField_gpr0.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & gpr0)).replaceAll(" ", "0"));
		gui.textField_gpr1.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & gpr1)).replaceAll(" ", "0"));
		gui.textField_gpr2.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & gpr2)).replaceAll(" ", "0"));
		gui.textField_gpr3.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & gpr3)).replaceAll(" ", "0"));
		gui.textField_ixr1.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & ixr1)).replaceAll(" ", "0"));
		gui.textField_ixr2.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & ixr2)).replaceAll(" ", "0"));
		gui.textField_ixr3.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & ixr3)).replaceAll(" ", "0"));
		gui.textField_pc.setText(String.format("%12s", Integer.toBinaryString(0xFFFF & ixr1)).replaceAll(" ", "0"));
		gui.textField_mar.setText(String.format("%12s", Integer.toBinaryString(0xFFFF & mar)).replaceAll(" ", "0"));
		gui.textField_mbr.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & mbr)).replaceAll(" ", "0"));
		gui.textField_ir.setText(String.format("%16s", Integer.toBinaryString(0xFFFF & ir)).replaceAll(" ", "0"));
		gui.textField_mfr.setText(String.format("%4s", Integer.toBinaryString(0xFFFF & mfr)).replaceAll(" ", "0"));
		gui.textField_cc.setText(String.format("%4s", Integer.toBinaryString(0xFFFF & cc)).replaceAll(" ", "0"));
	}
	
	// load in the IPL, and parse through each line to get the address and instruction. 
	// they were then added into the memory. 
	public void iPL() throws IOException {
		File file = new File("program.txt");
		String line;
		int address = 0;
		short instruction = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			line = br.readLine();
			while (line != null) {
				address = Integer.parseInt(line.substring(0,4), 16);
				instruction = (short) Integer.parseInt(line.substring((line.length() - 3), (line.length() - 0)), 16);
				memory[address] = instruction;
				line = br.readLine();
			}
		} 
		} 
		
	
	// instructions for the run button to function. It fetches, decodes, and excecutes. I talk more about these stages below. 
	// 
	public void runInstructions() {
		Instructions instruction;
		while (true) {
			fetch();
			instruction = decode(ir);
			execute(instruction);
			updateGUI();
			if (run==false) break;
			if (pc==2048) {
				mfr = 8;
				gui.textField_mfr.setText("1000");
				run=false;
				break;
			}
		}
	}
	
	// single instruction. where the ir gets loaded into the mbr, the CPU just decodes, and executes without fetching. When the run button is pressed. 
	// a doClick(); happens so that it immediately stops after one cycle. 
	public void ssInstruction() {
		Instructions instruction;
		ir = mbr;
		instruction = decode(ir);
		execute(instruction);
		updateGUI();
		if (gui.tgl_run.isSelected()) {
			gui.tgl_run.doClick();
		}
	}
	
	/*
	 * Each computer's CPU can have different cycles based on different instruction sets, 
	 * but will be similar to the following cycle:
	 * 
	 * Fetch Stage: The next instruction is fetched from the memory address that is currently stored in 
	 * the program counter and stored into the instruction register. At the end of the fetch operation, 
	 * the PC points to the next instruction that will be read at the next cycle.
	 * 
	 * Decode Stage: During this stage, the encoded instruction presented in the instruction register 
	 * is interpreted by the decoder. 
	 * 
	 * Execute Stage: The control unit of the CPU passes the decoded information as a sequence of control 
	 * signals to the relevant function units of the CPU to perform the actions required by the instruction, 
	 * such as reading values from registers, passing them to the ALU to perform mathematical or logic functions on them, and writing the result back to a register. 
	 * If the ALU is involved, it sends a condition signal back to the CU. The result generated by the operation is stored in the main memory or sent to an output device. 
	 * Based on the feedback from the ALU, the PC may be updated to a different address from which the next instruction will be fetched.
	 */
	
	// fetch: address in pc copied to the mar -> pc incremented to point to the next instruction -> instruction found at MAR's address, which is copied by the mbr. 
	// the mbr instrucion is then copied by the ir. 
	public void fetch() {
		mar = pc;
		mbr = memory[mar];
		ir = mbr;
		pc++;
	}
	
	// decode: Computer decodes the contents of the ir (done in the instructions class)
	public Instructions decode(short ir) {
		Instructions instruction = new Instructions(ir);
		return instruction;
	}
	
	// execute: computer sends signals to relevant components. 
	// cases for each opcode which will run its respective instruction. 
	public void execute(Instructions instruction) {
		
		if (instruction.opcode == 0) {
			hlt();
		}
		
		if (instruction.opcode == 1) {
			ldr(instruction);
		}
		
		if (instruction.opcode == 2) {
			str(instruction);
		}
		
		if (instruction.opcode == 3) {
			lda(instruction);
		}
		
		if (instruction.opcode == 41) {
			ldx(instruction);
		}
		
		if (instruction.opcode == 42) {
			stx(instruction);
		}
		
		if (instruction.opcode == 10) {
			jz(instruction);
		}
		
		if (instruction.opcode == 11) {
			jne(instruction);
		}
		
		if (instruction.opcode == 12) {
			System.out.println("YEs");
			jcc(instruction);
		}
		
	}
	
	// if the condition is 0. the running state stops. and the run button is deselected to stop everything. 
	public void hlt() {
		run = false;
		if (gui.tgl_run.isSelected()) gui.tgl_run.doClick();			
	}
	
	// ldr: Load Register From Memory. 4 conditions as there are 4 ways the GPR register instructions can appear. 00, 01, 10, 11. 
	public void ldr(Instructions instruction) {
		
		if (instruction.gprcode == 0) {
			gpr0 = memory[instruction.address];
		}
		
		if (instruction.gprcode == 1) {
			gpr1 = memory[instruction.address];
		}
		
		if (instruction.gprcode == 2) {
			gpr2 = memory[instruction.address];
		}
		
		if (instruction.gprcode == 3) {
			gpr3 = memory[instruction.address];
		}
	
	}
	
	// str: Store Register To Memory. same thing but other way around where the memory[] = to the contents of the register. 
	public void str(Instructions instruction) {
		
		if (instruction.gprcode == 0) {
			memory[instruction.address] = gpr0;
		}
		
		if (instruction.gprcode == 1) {
			memory[instruction.address] = gpr1;
		}
		
		if (instruction.gprcode == 2) {
			memory[instruction.address] = gpr2;
		}
		
		if (instruction.gprcode == 3) {
			memory[instruction.address] = gpr3;
		}

	}
	
	// LDA: Load Register with Address. the register gets the address straight up
	public void lda(Instructions instruction) {
		
		if (instruction.gprcode == 0) {
			gpr0 = instruction.address;
		}
		
		if (instruction.gprcode == 1) {
			gpr1 = instruction.address;
		}
		
		if (instruction.gprcode == 2) {
			gpr2 = instruction.address;
		}
		
		if (instruction.gprcode == 3) {
			gpr3 = instruction.address;
		}
	}
	
	// ldx: Load Index Register from Memory: 3 cases, as 00 would not load from a Index register. 
	public void ldx(Instructions instruction) {
		
		if (instruction.ixrcode == 1) {
			ixr1 = memory[instruction.address];
		}
		
		if (instruction.ixrcode == 2) {
			ixr2 = memory[instruction.address];
		}
		
		if (instruction.ixrcode == 3) {
			ixr3 = memory[instruction.address];
		}
	}
	
	// stx: Store Index Register to Memory. same thing but other way around :)
	public void stx(Instructions instruction) {
		
		if (instruction.ixrcode == 1) {
			memory[instruction.address] = ixr1;
		}
		
		if (instruction.ixrcode == 2) {
			memory[instruction.address] = ixr2;
		}
		
		if (instruction.ixrcode == 3) {
			memory[instruction.address] = ixr3;
		}
	}
	
	// jz: Jump If Zero: if contents of r = 0 (gprcode == 0) then pc = EA, else: pc <- PC+1
	public void jz(Instructions instruction) {
		
		if (instruction.gprcode == 0) {
			pc = instruction.address;
		}
		
		else {
			pc++;
		}
	}
	
	// jne: Jump If not equal: if contents of r != 0 (gprcode == 0) then pc = EA, else: pc <- PC+1
	public void jne(Instructions instruction) {
		
		if (instruction.gprcode != 0) {
			pc = instruction.address;
		}
		
		else {
			pc++;
		}
	}
	
	//jce: Jump If Condition Code: cc replaces r, and loads it into the register. if cc bit = 1 PC <- EA, else: pc++
	public void jcc(Instructions instruction) {
		short ccN = instruction.gprcode;
		
		if (ccN == 1) {
			pc = instruction.address;
		}
		
		else {
			pc++;
		}
	}
	
	
}

