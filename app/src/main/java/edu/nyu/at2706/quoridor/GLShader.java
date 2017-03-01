package edu.nyu.at2706.quoridor;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GLShader {
    // Program variables
    public static int sp_Image;


    public static int mPositionHandle;
    public static int mColorHandle;
    public static int mTexCoordLoc;
    public static int mtrxhandle;
    public static int mSamplerLoc;



    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // return the shader
        return shader;
    }

    public static String readFromRawResource(Context c, int resID){
        final InputStream inputStream = c.getResources().openRawResource(resID);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }

    public static void setHandles(){
        mPositionHandle = GLES20.glGetAttribLocation(GLShader.sp_Image, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(GLShader.sp_Image, "a_Color");
        mTexCoordLoc = GLES20.glGetAttribLocation(GLShader.sp_Image, "a_texCoord");
        mtrxhandle = GLES20.glGetUniformLocation(GLShader.sp_Image, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(GLShader.sp_Image, "s_texture");
    }
}
