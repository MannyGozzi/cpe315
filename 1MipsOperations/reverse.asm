# Name: Manuel Gozzi, Josh Levin
# Section: Lab 1-2
# Description:  Outputs the decimal number of the input as if it was interpreted as reverse binary formatß

#import java.util.Scanner;
#
#public class Main {
#    public static void main(String[] args) {
#        Scanner input = new Scanner(System.in);
#        System.out.print("Enter a decimal number: ");
#        int num = input.nextInt();
#        revBinToDec(num);
#    }
#
#    public static void revBinToDec(int num) {
#        int orig = num;
#        int mask = 1;
#        int res = 0;
#        for (int i = 0; i < 31; ++i) {
#            res += mask & num;
#            num = num >> 1;
#            res = res << 1;
#        }
#
#        System.out.println(orig + " -> " + res);
#    }
#}


# declare global so programmer can see actual addresses.
.globl num_prompt
.globl answer

#  Data Area (this area contains strings to be displayed during the program)
.data
num_prompt:
	.asciiz "Enter 16 bit reversed binary number: "
answer: 
	.asciiz "Answer: "
	
.text

.main
	# Prompt for number
	ori     $v0, $0, 5		
	la     $a0, num_prompt
	syscall
	
	# store number in $t1
	add $t1, $0, $v0
	
	# create counter
	addi $t3, $0, 31
	
	# set result to 0
	addi $t5, $0, 0  
	
	loop:
		beqz $t3 end
		andi  $t4, $t1, 1 # get rightmost number
		add $t5, $t5, $t4 # add the bit masked rightmost number to result
		sll $t5, $t5, 1 # shift result left 1
		addi $t3, $t3, -1 # decrement counter
		srl  $t1 $t1 1 # right shift input number
		j loop
		
	end:
	

	# Answer
	ori     $v0, $0, 4			
	la     $a0, answer
	syscall

	# print result
	ori $v0, $0, 1		
	add $a0, $t5, $0
	syscall

	# Exit (load 10 into $v0)
	ori $v0, $0, 10
	syscall
