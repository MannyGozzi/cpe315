add $a0, $0, $0
lw $a0 0($a0)
beq $a0 $a0 next    # 1 cycle penalty

next:	addi $a0, $0, 100
	addi $a1, $0, 101
    addi $a2, $0, 101
equal1:
