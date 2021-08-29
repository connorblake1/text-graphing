import java.util.Scanner;
public class BruteForceGraphing
{
    public static void main(String[] args) {
        Scanner keyboardReader = new Scanner(System.in);

        int range, domain, yIntercept, modeInt; //modeInt(1 = line mode, 2 = parabola mode)
        boolean mode = true; //true = line mode, false = parabola mode
        double slope, aTerm, bTerm, cTerm;
        while (true) {
            boolean modeSelector = true;
            while (modeSelector) {
                System.out.println("Enter 1 for Line Mode and 2 for Parabola Mode.");
                modeInt = keyboardReader.nextInt();
                if (modeInt == 1) {
                    modeSelector = false;
                    mode = true; }
                else if (modeInt == 2) {
                    modeSelector = false;
                    mode = false;}
                else
                    System.out.println("Please enter a valid value");}
            //line mode
            if (mode) {
                System.out.print("Please enter the range: ");
                range = keyboardReader.nextInt();

                System.out.print("Please enter the domain: ");
                domain = keyboardReader.nextInt();

                System.out.print("Please enter the y-intercept: ");
                yIntercept = keyboardReader.nextInt();

                System.out.print("Please enter the slope: ");
                slope = keyboardReader.nextDouble();

                //----------------------------------
                // TODO: You write the rest!
                System.out.println(range); //print range at top
                for (int y = range; y >= 0; y--) {
                    for (int x = 0; x < domain; x++) {
                        Character printChar = ' ';
                /*ok so i'm going to use the distance of the point to the closest point on the line,
                but i'm going to use vector projections because geometry is annoying. This will hopefully
                create the set of points that would almost make the actual line the "line of best fit" through the points graphed*/
                        double lineX = domain;
                        double lineY = domain * slope;
                        int pointX = x;
                        int pointY = y - yIntercept;
                /*the first vector only holds the slope and the other is Vector(point) normalized to the origin
                the equation for the distance of a point to a line (in pseudocode) is sqrt(sq(magnitude(pointVector))-
                 sq(dotProduct(lineVector,pointVector)/magnitude(lineVector)), using the pythagorean theorem on the point
                the scalar projection of the line in order to find the distance to the line
                The real equation is going to look a lot messier
                 */
                        double d = Math.sqrt(Math.pow(pointX, 2) + Math.pow(pointY, 2) + 0.000000001 - Math.pow((lineX * pointX + pointY * lineY) / (Math.sqrt(Math.pow(lineX, 2) + Math.pow(lineY, 2))), 2));
                    /*So, you may be wondering why the random 0.000000001 is thrown in there. Well, because computers suck at double calculations, this
                    section would throw NaN when it was really close because sqrt(80-80.00000000000001) is still imaginary. Figuring this out was
                     excruciating.*/
                        if (d < .5) //maximum distance from the correct line a point should fall
                            printChar = '*';
                        else if (y == 0 && x == 0)
                            printChar = '+';
                        else if (x == 0)
                            printChar = '|';
                        else if (y == 0)
                            printChar = '-';
                        System.out.print(printChar);
                    }
                    if (y == 0)
                        System.out.print(domain);
                    System.out.println();
                }
            }
            //<parabola mode>
            else {
                System.out.print("Please enter the range: ");
                range = keyboardReader.nextInt();

                System.out.print("Please enter the domain: ");
                domain = keyboardReader.nextInt();

                System.out.print("Please enter the a term of the parabola (ax^2 + bx + c): ");
                aTerm = keyboardReader.nextDouble();

                System.out.print("Please enter the b term of the parabola (ax^2 + bx + c): ");
                bTerm = keyboardReader.nextDouble();

                System.out.print("Please enter the c term of the parabola (ax^2 + bx + c): ");
                cTerm = keyboardReader.nextDouble();
                //----------------------------------
                //<begin old version>
                // TODO: You write the rest!
                System.out.println(range); //print range at top
                for (int y = range; y >= 0; y--) {
                    for (int x = 0; x < domain; x++) {
                        Character printChar = ' ';
                /*These calculations are going to be even spicier than the last ones: I am going to use derivatives to optimize the
                distance from the point to the parabola in order to find its closest approach to the line. In pseudocode, I am optimizing
                the following equation for x about the point (h,k) (or, in the code here, (x,y)): S = sqrt(sq(x-h)+sq(ax^2+bx+c-k)). However, the sqrt() surrounding
                it all doesn't actually do that much in terms of calculus, so we'll ignore it (thanks, math stack exchange). Thus, the
                factored derivative of this (dS/dx) is a cubic x^3(4a^2) + x^2(6ab) + x(2+4ac-4ak+2b^2) + 1(2bc-2kb-2h). These coefficients
                are going to be reassigned to the new a1,b1,c1,d1 so that I can proceed with Newton's Method, a root finding algorithm I used
                to make that fractal I emailed you with the math club logo.
                First: the redefined coefficients of the cubic function that is the derivative of the distance to the parabola with respect to x:
                 */
                        double a1 = 4*aTerm*aTerm;
                        double b1 = 6*aTerm*bTerm;
                        double c1 = 2+4*aTerm*cTerm-4*aTerm*y+2*bTerm*bTerm;
                        double d1 = 2*bTerm*cTerm-2*y*bTerm-2*x;
                /* Because the cubic holds the derivative of the distance, I need to find where it is 0 to find the local minima and
                maxima of its approaches, one of which will be the shortest distance. Newton's method is going to return a bunch of guesses
                for the roots of the function which will be plugged into the distance formula and compared against the smallest distance yet. If
                that minimum is less than .5, then a point will finally be drawn.
                I know there are easier ways to do this, but this way has 0 drawbacks (other than computation time, which is horrendously slow), and it gave me an excuse to
                see what programming calculus tastes like. In addition, if I were to write a bit of a factoring program, I could generalize this to
                any polynomial. Also, this theoretically could be scaled up to make a knockoff desmos with very little
                modification.*/
                        double minGuess = Math.pow(domain*domain + range*range,.5); //sufficiently large number
                        for (float x1 = 0; x1 <domain; x1 += .1) { //go through the domain slowly
                            double guessX = x1;
                            for (int iteration = 0; iteration < 12;iteration++) {
                                //nasty Newton's method modification incoming... f(x)/f'(x) for the cubic
                                guessX -= (a1*Math.pow(guessX,3)+b1*Math.pow(guessX,2)+c1*guessX+d1)/(3*a1*Math.pow(guessX,2)+2*b1*guessX+c1);}
                            double d = Math.pow(Math.pow(guessX-x,2)+Math.pow(aTerm*Math.pow(guessX,2)+bTerm*guessX+cTerm-y,2),.5); //plugs the guess for the x coordinate into the distance eq
                            if (d < minGuess) {
                                minGuess = d;
                            }
                            }

                        if (minGuess < .5) //maximum distance from the correct line a point should fall
                            printChar = '*';
                        else if (y == 0 && x == 0)
                            printChar = '+';
                        else if (x == 0)
                            printChar = '|';
                        else if (y == 0)
                            printChar = '-';
                        System.out.print(printChar);
                    }
                    if (y == 0)
                        System.out.print(domain);
                    System.out.println();
                }
                //</end old version>
//                System.out.println(range); //print range at top
//                for (int y = range; y >= 0; y--) {
//                    for (int x = 0; x < domain; x++) {
//                        boolean close = false;
//                        Character printChar = ' ';
//                        if (close) //maximum distance from the correct line a point should fall
//                            printChar = '*';
//                        else if (y == 0 && x == 0)
//                            printChar = '+';
//                        else if (x == 0)
//                            printChar = '|';
//                        else if (y == 0)
//                            printChar = '-';
//                        System.out.print(printChar);
//                    }
//                    if (y == 0)
//                        System.out.print(domain);
//                    System.out.println();
//                }


            }
            //----------------------------------
        }
    }

}
