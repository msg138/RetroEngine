package com.retro.engine.model.shader;

import com.jogamp.opengl.GL2;
import com.retro.engine.Framework;

import java.awt.*;
import java.nio.FloatBuffer;

/**
 * Created by Michael on 7/30/2016.
 */
public class ShaderLoader {

    public static ShaderProgram defaultShaderColor3d = null;
    public static ShaderProgram defaultShaderColor2d = null;
    public static ShaderProgram defaultShaderImage3d = null;
    public static ShaderProgram defaultShaderImage2d = null;

    public static ShaderProgram currentShader = null;

    public static void loadDefaultShaders(GL2 gl) {
        {
            ShaderProgram def = new ShaderProgram("#version 330\n" +
                    "layout(location=0) in vec3 position;\n" +
                    "layout(location=1) in vec3 color;\n" +
                    "layout(location=3) in vec3 normal;\n" +
                    "uniform mat4 model;\n" +
                    "uniform mat4 view;\n" +
                    "uniform mat4 projection;\n" +
                    "uniform mat4 normalMatrix;\n" +
                    "uniform vec3 light;\n" +
                    "out vec4 vColor;\n" +
                    "out vec3 norm;\n" +
                    "out vec4 worldPos;\n" +
                    "out vec3 lightPos;\n" +
                    "out vec3 cameraPos;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "gl_Position = projection * view *  model * vec4(position, 1.0);\n" +
                    "vColor = vec4(color, 1.0);\n" +
                    "norm = (vec4(normal, 0.0)).xyz;\n" +
                    "worldPos = (model * vec4(position, 1.0));\n" +
                    "lightPos = light;\n" +
                    "cameraPos = (view * vec4(0.0,0.0,0.0,1.0)).xyz;\n" +
                    "}\n",

                    "#version 330\n" +
                            "in vec4 vColor;\n" +
                            "in vec3 norm;\n" +
                            "in vec4 worldPos;\n" +
                            "in vec3 cameraPos;\n" +
                            "out vec4 fColor;\n" +
                            "in vec3 lightPos;\n" +
                            "void main(void)\n" +
                            "{\n" +
                            "vec3 lightDir = vec3(lightPos - worldPos.xyz);\n" + // Incidence vector
                            "float intensity = max(dot(normalize(lightDir), normalize(norm)), 0.0);\n" +
                            "fColor = vColor * vec4(intensity, intensity, intensity, 1.0);\n" +
                            //"fColor = vColor;\n" +
                            "}\n", gl);

            def.updateProjectionMatrix(gl, 45.0f, Framework.getInstance().getGameWidth(), Framework.getInstance().getGameHeight(), .1f, 200.0f);
            defaultShaderColor3d = def;

            currentShader = def;
        }
        {
            ShaderProgram def = new ShaderProgram("#version 330\n" +
                    "layout(location=0) in vec2 position;\n" +
                    "layout(location=1) in vec3 color;\n" +
                    "uniform mat4 model;\n" +
                    "uniform mat4 view;\n" +
                    "uniform mat4 projection;\n" +
                    "out vec4 vColor;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "gl_Position = projection * vec4(position, 1.0, 1.0);\n" +
                    "vColor = vec4(color, 1.0);\n" +
                    "}\n", "#version 330\n" +
                    "in vec4 vColor;\n" +
                    "out vec4 fColor;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "fColor = vColor;\n" +
                    "}\n", gl);

            def.updateProjectionMatrixOrtho(gl, Framework.getInstance().getGameWidth(), Framework.getInstance().getGameHeight());
            defaultShaderColor2d = def;
        }
        {
            ShaderProgram def = new ShaderProgram("#version 330\n" +
                    "layout(location=0) in vec2 position;\n" +
                    "layout(location=1) in vec4 color;\n" +
                    "layout(location=2) in vec2 texcoord;\n" +
                    "uniform mat4 model;\n" +
                    "uniform mat4 view;\n" +
                    "uniform mat4 projection;\n" +
                    "out vec2 texCoord;\n" +
                    "out vec4 ccolor;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "gl_Position = projection * model * vec4(position, 1.0, 1.0);\n" +
                    "texCoord = texcoord;\n" +
                    "ccolor = color;\n" +
                    "}\n", "#version 330\n" +
                    "uniform sampler2D tex;\n" +
                    "in vec2 texCoord;\n" +
                    "in vec4 ccolor;\n" +
                    "out vec4 fColor;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "vec4 c = texture(tex, texCoord);\n" +
                    "c *= ccolor;\n" +
                    "if(c.w < 0.1)\n" +
                    "discard;\n" +
                    "else\n"+
                    "fColor = c;\n" +
                    "}\n", gl);

            def.updateProjectionMatrixOrtho(gl, Framework.getInstance().getGameWidth(), Framework.getInstance().getGameHeight());
            defaultShaderImage2d = def;
        }
        {
            ShaderProgram def = new ShaderProgram("#version 330\n" +
                    "layout(location=0) in vec3 position;\n" +
                    "layout(location=1) in vec4 color;\n" +
                    "layout(location=2) in vec2 texcoord;\n" +
                    "uniform mat4 model;\n" +
                    "uniform mat4 view;\n" +
                    "uniform mat4 projection;\n" +
                    "out vec2 texCoord;\n" +
                    "out vec4 ccolor;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "gl_Position = projection * view * model * vec4(position, 1.0);\n" +
                    "texCoord = texcoord;\n" +
                    "ccolor = color;\n" +
                    "}\n", "#version 330\n" +
                    "uniform sampler2D tex;\n" +
                    "in vec2 texCoord;\n" +
                    "in vec4 ccolor;\n" +
                    "out vec4 fColor;\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "vec4 c = texture(tex, texCoord);\n" +
                    "c *= ccolor;\n" +
                    "if(c.w < 0.1)\n" +
                    "discard;\n" +
                    "else\n"+
                    "fColor = c;\n" +
                    "}\n", gl);

            def.updateProjectionMatrix(gl, 45.0f, Framework.getInstance().getGameWidth(), Framework.getInstance().getGameHeight(), .1f, 200.0f);
            defaultShaderImage3d = def;
        }
    }

    public static void setActiveShader(ShaderProgram shader){
        currentShader = shader;
    }

    public static ShaderProgram getCurrentShader(GL2 gl){
        if(currentShader == null){
            if(defaultShaderColor3d == null){
                loadDefaultShaders(gl);
            }
            currentShader = defaultShaderColor3d;
        }
        return currentShader;
    }

    public static void updateViewMatrix(GL2 gl, float[] mat, ShaderProgram shader){
        shader.updateViewMatrix(gl, mat);
    }

    public static void updateModelMatrix(GL2 gl, float[] mat, ShaderProgram shader){
        shader.updateModelMatrix(gl, mat);
    }
}
