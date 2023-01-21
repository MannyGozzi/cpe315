# Name: Manuel Gozzi, Josh Levin
# Section: Lab 1-4
# Description:  Performs exponentiation

#public class Exponent {
#    public static void main(String[] args) {
#        exponent(5, 3);
#    }
#
#    public static void exponent(int x, int y) {
#        int answer = x;
#        int increment = x;
#        for (int i = 0; i < y - 1; ++i) {
#            for (int j = 0; j < x - 1; ++j) {
#                answer += increment;
#            }
#            increment = answer;
#        }
#        System.out.println("The product is " + answer);
#    }
#}


# declare global so programmer can see actual addresses.
.globl prompt1
.globl prompt2
.globl answer

#  Data Area (this area contains strings to be displayed during the program)
.data

prompt1:
	.asciiz "Enter a base: "

prompt2:
	.asciiz "Enter an exponent: "

answer: 
	.asciiz "Answer: "
	
.text

.main
	# Prompt num
	ori     $v0, $0, 4			
	la     $a0, prompt1
	syscall

	# get num
	ori $v0, $0, 5
	syscall
	ori $t0, $v0, 0


	# Prompt exponent
	ori     $v0, $0, 4			
	la     $a0, prompt2
	syscall

	# get exponent
	ori $v0, $0, 5
	syscall
	ori $t1, $v0, 0

	# initialize answer to base
	ori $t2, $t0, 0
	
	# initialize increment to base
	ori $t3, $t0, 0
	
	# intialize i for outer loop
	addi $t4, $t1, -1
	
	exponent:
		beqz $t4, end
		# initialize j for inner loop
		addi $t5, $t0, -1
		start_add:
			beqz $t5, end_add
			add $t2, $t2, $t3
			addi $t5, $t5, -1
			j start_add
		
		end_add:
		addi $t4, $t4, -1
		ori $t3,$t2, 0
		j exponent
		
	end:
	

	# Display the answer message (load 4 into $v0 to display)
	ori     $v0, $0, 4			
	la     $a0, answer
	syscall

	# print product
	ori $v0, $0, 1			
	add $a0, $t2, $0
	syscall

	# Exit (load 10 into $v0)
	ori $v0, $0, 10
	syscall
