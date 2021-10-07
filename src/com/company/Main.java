package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    double[] initPos;
    double[][] matrixSystem = new double[8][9];
    double[][] modMatrix = new double[3][3];

    int width = 1200;
    int height = 400;
    double[][] image = new double[height][width];
    double[] preImage;
    int[] x = new int[4];
    int[] y = new int[4];
    int fromW,fromH;
    double w;
    double[] u = new double[]{0, width, 0, width};
    double[] v = new double[]{0, 0, height, height};
    double q = 1;
    BufferedImage preImg = null;
    Color[][] imageColor;
    String pathname = "banner.jpg";

    ArrayList<Row> solution;
    public static void main(String[] args) {
        new Main().run();
    }
    public void run(){
        // write your code here
        try {
            preImg = ImageIO.read(new File(pathname));
        } catch (IOException e) {
        }
        fromW = preImg.getWidth();
        fromH = preImg.getHeight();
        System.out.println(fromW + " " + fromH);
        System.out.println(fromH);
        setup();
        int clicked = 0;

        boolean pressing = false;
        StdDraw.setPenColor(StdDraw.RED);
        while(clicked < 4){
            StdDraw.clear();
            StdDraw.picture(fromW/2,fromH/2,pathname);
            StdDraw.text(100,100,StdDraw.mouseX() +", " + StdDraw.mouseY());
            if(clicked == 1){
                StdDraw.line(x[0],y[0],StdDraw.mouseX(),StdDraw.mouseY());
            }else if(clicked == 2){
                StdDraw.line(x[0],y[0],x[1],y[1]);
                StdDraw.line(x[0],y[0],StdDraw.mouseX(),StdDraw.mouseY());
            }else if(clicked == 3){
                StdDraw.line(x[0],y[0],x[1],y[1]);
                StdDraw.line(x[0],y[0],x[2],y[2]);
                StdDraw.line(x[1],y[1],StdDraw.mouseX(),StdDraw.mouseY());
                StdDraw.line(x[2],y[2],StdDraw.mouseX(),StdDraw.mouseY());
            }
            if(StdDraw.isMousePressed()){
                if(!pressing){
                    pressing = true;
                    x[clicked] = (int)StdDraw.mouseX();
                    y[clicked] = (int)StdDraw.mouseY();
                    clicked += 1;
                    System.out.println(clicked + ": (" + StdDraw.mouseX()+"," + StdDraw.mouseY() + ")");
                }
            }else{
                pressing = false;
            }
            StdDraw.show();
        }

        for (int r = 0; r < 8; r++) {
            if (r < 4) {
                matrixSystem[r][0] = u[r];
                matrixSystem[r][1] = v[r];
                matrixSystem[r][2] = q;
                matrixSystem[r][6] = -u[r] * x[r];
                matrixSystem[r][7] = -v[r] * x[r];
                matrixSystem[r][8] = x[r];
            } else {
                matrixSystem[r][3] = u[r - 4];
                matrixSystem[r][4] = v[r - 4];
                matrixSystem[r][5] = q;
                matrixSystem[r][6] = -u[r - 4] * y[r - 4];
                matrixSystem[r][7] = -v[r - 4] * y[r - 4];
                matrixSystem[r][8] = y[r-4];
            }
        }
        //solve matrix
        matrixSystem = Matrix.rref(matrixSystem);
        for(int r = 0; r < 3; r++){
            for(int c = 0; c < 3; c++){
                if(r*3+c < 8) {
                    modMatrix[r][c] = matrixSystem[r * 3 + c][8];
                }else{
                    modMatrix[r][c] = 1;
                }
            }
        }


        double[][] temp = new double[3][1];
        double[][] from;
        imageColor = new Color[image.length][image[0].length];
        for(int r = 0; r < height; r++){
            for(int c = 0; c < width; c++){
                temp[0][0] = c;
                temp[1][0] = r;
                temp[2][0] = q;
                from = Matrix.multiply(modMatrix,temp);
                from[1][0] /= from[2][0];
                from[0][0] /= from[2][0];

                from[1][0] = fromH - from[1][0];
                for(int i = 0; i < 2 ;i++){
                    if(from[i][0] <= 0){
                        from[i][0] = 0;
                    }else if(from[0][0] > fromW-1){
                        from[0][0] = fromW-1;
                    }else if(from[1][0] > fromH-1){
                        from[1][0] = fromH-1;
                    }
                }
                //System.out.println(from[1][0] + " " + from[0][0]);

                //System.out.println(Arrays.toString(Matrix.transpose(from)[0]));
                imageColor[r][c] = new Color(preImg.getRGB((int)Math.round(from[0][0]),(int)Math.round(from[1][0])));
                //System.out.println(imageColor[r][c]);
            }
        }
//        for(int i = 0; i < 4; i++) {
//            double[][] t = new double[][]{{u[i]}, {v[i]}, {q}};
//            double[][] tem = Matrix.multiply(modMatrix, t);
//            System.out.println("\n" + x[i] + " " + y[i]);
//            double[] tempo = Matrix.transpose(tem)[0];
//            tempo[0]/=tempo[2];
//            tempo[1]/=tempo[2];
//            System.out.println(Arrays.toString(tempo));
//        }
//        for (int i = 0; i < 3; i++) {
//            System.out.println(Arrays.toString(modMatrix[i]));
//        }

        visual();

        StdDraw.show();
    }
    public double[][] rref(double[][] matrix){
        int r = 0;
        int c = 0;
        solution = new ArrayList<Row>();
        for(int i = 0; i < matrix.length; i++){
            solution.add(new Row(matrix[i]));
        }
        reorganize();
        while (r < matrix.length){
            //solution is an arrayList<Row>. Each row contains an array of doubles (named content).
            if(solution.get(r).content[c] > 0){
                if(solution.get(r).content[c] != 1){
                    solution.get(r).multiplyBy(1/solution.get(r).content[c]);//2*2 = 4, 4 mod 3 = 1
                }
                for(int nr = 0; nr < matrix.length; nr++){
                    //row reduce if not 0
                    if(nr!= r&&solution.get(nr).content[c] != 0){
                        double temp = solution.get(nr).content[c];
                        solution.get(r).multiplyBy(temp);//in case not 1
                        solution.get(nr).minus(solution.get(r));
                        solution.get(r).multiplyBy(1/temp);
                    }
                }
            }
            r += 1;
            c +=1;
            reorganize();//coded a class and do Collections.sort();
        }
        String output = "";
        for(int i = 0; i < solution.size(); i++){
            output += Arrays.toString(solution.get(i).content)+"\n";
        }
        System.out.println(output);
        for(int i = 0; i < matrix.length; i++){
            matrix[i] = solution.get(i).output();
        }
        return matrix;
    }
    public void reorganize(){
        Collections.sort(solution);
        Collections.reverse(solution);
    }
    public void setup(){
        StdDraw.setCanvasSize(700,700);
        StdDraw.setXscale(0,700);
        StdDraw.setYscale(0,700);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void visual(){
        StdDraw.clear();
        for(int r = 0; r < imageColor.length; r++){
            for(int c = 0; c < imageColor[r].length; c++){
                StdDraw.setPenColor(imageColor[r][c]);
                StdDraw.filledRectangle(c*0.5+0.25,500.50-r*0.5,0.25,0.25);
            }
        }
        StdDraw.show();
    }
}
