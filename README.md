# Primus

Primus is a programmable calculator that supports arbitrary-precision arithmetic. Primus features a scripting GUI made in JavaFX, and a recursive decent parser that is able to parse user-defined functions which contain multiple arguments.

![example1](https://imgur.com/SovArnA.png)

## Sample Programs

Calculate hypotenuse:
```
method var hypotenuse(var a, var b)
	return eval(sqrt(a^2 + b^2))

eval hypotenuse(3, 4)
```
>Output:
>5

Approximate integral:
```
method var integral(function f(x), var a, var b, var stepSize)
	var s = 0
	for var i = a:eval(i < b):i+stepSize
		s := s+ (f(i) * stepSize)
	return s

eval integral(x*x, 0, 10, 0.05)
```
>Output:
>330.837500
