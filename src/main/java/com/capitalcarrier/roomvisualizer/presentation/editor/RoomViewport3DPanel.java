package com.capitalcarrier.roomvisualizer.presentation.editor;

import com.capitalcarrier.roomvisualizer.domain.model.FurnitureItem;
import com.capitalcarrier.roomvisualizer.domain.model.Room;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import java.awt.*;

public class RoomViewport3DPanel extends GLJPanel implements GLEventListener {
    private Room room;
    private GLU glu;
    private float rotationX = 20;
    private float rotationY = -30;
    private float zoom = -15f;

    public RoomViewport3DPanel(Room room) {
        super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
        this.room = room;
        addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0.05f, 0.08f, 0.2f, 1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        
        float[] lightPos = { 5.0f, 10.0f, 10.0f, 1.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0, 0, zoom);
        gl.glRotatef(rotationX, 1, 0, 0);
        gl.glRotatef(rotationY, 0, 1, 0);

        // Center room in 3D
        gl.glTranslatef(-(float)room.getWidth()/2, -(float)room.getHeight()/4, -(float)room.getLength()/2);

        drawRoom(gl);
        
        for (FurnitureItem item : room.getFurnitureItems()) {
            drawFurniture(gl, item);
        }
    }

    private void drawRoom(GL2 gl) {
        // Floor
        gl.glColor3f(0.4f, 0.3f, 0.2f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f((float)room.getWidth(), 0, 0);
        gl.glVertex3f((float)room.getWidth(), 0, (float)room.getLength());
        gl.glVertex3f(0, 0, (float)room.getLength());
        gl.glEnd();

        // Walls (Simple wireframe or semi-transparent)
        gl.glColor4f(0.8f, 0.8f, 0.9f, 0.3f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(0, 0, 0); gl.glVertex3f(0, (float)room.getHeight(), 0);
        gl.glVertex3f((float)room.getWidth(), 0, 0); gl.glVertex3f((float)room.getWidth(), (float)room.getHeight(), 0);
        gl.glEnd();
    }

    private void drawFurniture(GL2 gl, FurnitureItem item) {
        gl.glPushMatrix();
        gl.glTranslatef((float)item.getX(), (float)item.getY(), (float)item.getZ());
        gl.glRotatef((float)item.getRotation(), 0, 1, 0);
        
        gl.glColor3f(0.6f, 0.2f, 1.0f);
        drawCube(gl, (float)item.getWidth(), (float)item.getHeight(), (float)item.getDepth());
        
        gl.glPopMatrix();
    }

    private void drawCube(GL2 gl, float w, float h, float d) {
        gl.glBegin(GL2.GL_QUADS);
        // Front
        gl.glVertex3f(0, 0, d); gl.glVertex3f(w, 0, d); gl.glVertex3f(w, h, d); gl.glVertex3f(0, h, d);
        // Back
        gl.glVertex3f(0, 0, 0); gl.glVertex3f(0, h, 0); gl.glVertex3f(w, h, 0); gl.glVertex3f(w, 0, 0);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (double) width / height, 0.1, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
}
