def evaluate_polynomial(coeffs, x):
    if coeffs == []:
        return 0.0
    res = 0.0  
    for coef in reversed(coeffs):
        res = res * x + coef
    return res

print(evaluate_polynomial([], 2))                  
print(evaluate_polynomial([5], 10))                
print(evaluate_polynomial([0, 0, 0], 3))           
print(evaluate_polynomial([3, -2, 0, 5], 2.0))      
print(evaluate_polynomial([1, 1, 1, 1], 2))        
print(evaluate_polynomial([-1, 0, 1], 3))           