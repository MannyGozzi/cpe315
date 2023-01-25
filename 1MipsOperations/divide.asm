# Name: Manuel Gozzi, Josh Levin
# Section: Lab 1-3
# Description:  Performs divison on a 64 bit number with the assumption that the divisor is of base 2

# import java.util.Scanner;
# 
# public class Division {
#     public static void main(String[] args) {
#         Scanner input = new Scanner(System.in);
#         int left = input.nextInt();
#         int right = input.nextInt();
#         int div = input.nextInt();
# 
#         int rightmost = 1;
#         int leftmost = -2147483648;
#         while (div != 1) {
#             // while we don't see a 1 in the rightmost portion of the right number
#             // shift the left and right as a single unit 1 position to the right
#             right = right >>> 1;
#             int overflow = rightmost & left;
#             left = left >> 1;
#             if (overflow > 0) right = right | leftmost;
#             div = div >> 1;
#         }
# 
#         System.out.println(left);
#         System.out.println(right);
#     }
# }



# declare global so programmer can see actual addresses.
.globl num_prompt
.globl num2_prompt
.globl answer
.globl div_prompt
.globl comma

#  Data Area (this area contains strings to be displayed during the program)
.data
num_prompt:
	.asciiz "Enter high 32 bit num: "
num2_prompt:
	.asciiz "Enter low 32 bit num: "
div_prompt:
	.asciiz "Enter divisor: "
answer: 
	.asciiz "Answer: "
comma: 
	.asciiz ","
.text

.main
	# Prompt for num, store in $t1 (left)
	ori     $v0, $0, 4			
	la     $a0, num_prompt
	syscall
	ori $v0, $0, 5
	syscall
	ori $t1, $v0, 0
	
	# Prompt for num2, store in $t2 (right)
	ori     $v0, $0, 4			
	la     $a0, num2_prompt
	syscall
	ori $v0, $0, 5
	syscall
	ori $t2, $v0, 0
	
	# Prompt for div, store in $t3
	ori     $v0, $0, 4			
	la     $a0, div_prompt
	syscall
	ori $v0, $0, 5
	syscall
	ori $t3, $v0, 0
	
	addi $t4, $0, 1            # rightmost mask,
	addi $t5, $0, -2147483648  # leftmost mask

	loop: beq $t3, 1, end
		srl $t2, $t2, 1           # right = right >>> 1
		and $t6, $t4, $t1         # overflow = rightmost & left;
		srl $t1, $t1, 1           # left = left >> 1;
		beq  $t6, 0, noflow
			or $t2, $t2, $t5  # right = right | leftmost;
		noflow:
		srl $t3, $t3, 1           # div = div >> 1;
		j loop
	end:
	

	# Answer
	ori     $v0, $0, 4			
	la     $a0, answer
	syscall

	# print result 1
	ori $v0, $0, 1		
	add $a0, $t1, $0
	syscall
	
	# comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	
	# print result 2
	ori $v0, $0, 1		
	add $a0, $t2, $0
	syscall

	# Exit (load 10 into $v0)
	ori $v0, $0, 10
	syscall
