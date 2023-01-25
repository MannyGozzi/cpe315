# Name: Manuel Gozzi, Josh Levin
# Section: Lab 1-1
# Description:  Performs modulus under the assumption the divisor is a power of 2

# int mod(int num, int div) {
#   int mask = div - 1;
#   int mod = num & mask;
#   System.out.println(mod);
# }

# declare global so programmer can see actual addresses.
.globl num_prompt
.globl div_prompt
.globl answer

#  Data Area (this area contains strings to be displayed during the program)
.data

num_prompt:
	.asciiz "Enter a number: "

div_prompt:
	.asciiz "Enter a modulus divisor: "

answer: 
	.asciiz "Answer: "
	
.text

.main
	# Display the num_prompt message (load 4 into $v0 to display)
	ori     $v0, $0, 4			
	# This generates the starting address for the prompt message.
	# (assumes the register first contains 0).
	la     $a0, num_prompt
	syscall

	# get num
	ori $v0, $0, 5
	syscall
	ori $t0, $v0, 0


	# Display the num_prompt message (load 4 into $v0 to display)
	ori     $v0, $0, 4			
	# This generates the starting address for the prompt message.
	# (assumes the register first contains 0).
	la     $a0, div_prompt
	syscall
	
	# get div
	ori $v0, $0, 5
	syscall
	ori $t1, $v0, 0

	# mask = div -1
	add $t2, $t1, -1

	# solve for the mod
	#   mod = num & mask
	and $t3, $t0, $t2

	# Display the answer message (load 4 into $v0 to display)
	ori     $v0, $0, 4			
	# This generates the starting address for the prompt message.
	# (assumes the register first contains 0).
	la     $a0, answer
	syscall

	# print modulus
	ori $v0, $0, 1			
	add $a0, $t3, $0
	syscall

	# Exit (load 10 into $v0)
	ori $v0, $0, 10
	syscall
