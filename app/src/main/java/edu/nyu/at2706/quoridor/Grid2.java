package edu.nyu.at2706.quoridor;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Size;

import java.nio.ShortBuffer;

public class Grid2 {
    private short[] indices;
    public ShortBuffer drawListBuffer;
    public int bufferIdindicies;

    private final float[] matrix = new float[16];

    public Size gridSize;               // Standard 9x9 grid
    public float thickness, length;     // Dimensions of the line

    public float[] vertexData;

    public Grid2(int width, int height) {
        Matrix.setIdentityM(matrix, 0);
        gridSize = new Size(width, height);
    }

    public void glBufferInit(){
        int totalVertices = gridSize.getWidth()* gridSize.getHeight() * 24;
        vertexData = new float[totalVertices];
        int i = 0;
        for(int x = 0; x < gridSize.getWidth(); x++)
            for (int y = 0; y < gridSize.getHeight(); y++) {
                final float depth = 0;
                /* Horizontal */
                // Top Left Corner
                vertexData[++i] = length * x;
                vertexData[++i] = (length - thickness) * y;
                vertexData[++i] = depth;

                // Bottom Left Corner
                vertexData[++i] = length * x;
                vertexData[++i] = length * (y + 1);
                vertexData[++i] = depth;

                // Bottom Right Corner
                vertexData[++i] = length * (x + 1);
                vertexData[++i] = length * (y + 1);
                vertexData[++i] = depth;

                // Top Right Corner
                vertexData[++i] = length * (x + 1);
                vertexData[++i] = (length - thickness) * y;
                vertexData[++i] = depth;

                /* Vertical */
                // Top Left Corner
                vertexData[++i] = (length - thickness) * (x + 1);
                vertexData[++i] = length * y;
                vertexData[++i] = depth;

                // Bottom Left Corner
                vertexData[++i] = length * x;
                vertexData[++i] = length * (y + 1);
                vertexData[++i] = depth;

                // Bottom Right Corner
                vertexData[++i] = length * (x + 1);
                vertexData[++i] = length * (y + 1);
                vertexData[++i] = depth;

                // Top Right Corner
                vertexData[++i] = length * (x + 1);
                vertexData[++i] = y;
                vertexData[++i] = depth;

            }
    }
}
