# Primus

Primus is a programmable calculator that supports arbitrary-precision arithmetic. Primus features a scripting GUI made in JavaFX, and a recursive decent parser that is able to parse user-defined functions which contain multiple arguments.

![example1](https://imgur.com/OtQr5gL.png)

## Sample Programs

Calculate hypotenuse:
```
method var hypotenuse(defVar a, defVar b)
	return eval(sqrt(a^2 + b^2))

eval hypotenuse(3, 4)
```
>Output:
>5

Approximate integral:
```
method var integral(defFunction f(x), defVar a, defVar b, defVar steps)
	defVar sum = 0
	defVar stepSize = steps/(b-a)
	for defVar i = a:eval(i <= b):i+stepSize
		defVar sum = sum + (f(i) * stepSize)
	return sum

eval integral(x*x, 0, 10, 0.1)
```
>Output:
>333.833500
