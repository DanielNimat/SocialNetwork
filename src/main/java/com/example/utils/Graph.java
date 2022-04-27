package com.example.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    private int[][] graph;
    private int[] visited;
    private int[][] distances;

    public Graph(int[][] graph) {
        this.graph = graph;
    }

   public int getNumberOfConnectedComponents() {
        setConnectedComponentsArray();
        return Arrays.stream(visited).max().getAsInt();
   }

    private void setConnectedComponentsArray() {
        visited = new int[graph.length];
        int component = 1;
        for (int i = 0; i < graph.length; i++) {
            if (visited[i] == 0) {
                DFS(i, component);
                component++;
            }
        }
    }

    private void DFS(int x, int component)
    {
        visited[x] = component;
        for (int i=0; i< graph.length; i++) {
            if (graph[x][i] == 1 && visited[i] == 0)
                DFS(i, component);
        }
    }

    public List<Integer> verticesFromTheComponentWithTheLongestPath() {
        List<Integer> vertices = new ArrayList<>();
        setConnectedComponentsArray();
        setDistances();
        int maximumDistance = getMaximumDistance();
        int component = getComponentLongestPath(maximumDistance);
        for (int v=0; v<visited.length; v++)
            if (visited[v] == component)
                vertices.add(v);
        return vertices;
    }

    private void setDistances() {
        distances = graph;

        for (int k = 0; k < graph.length; k++)
        {
            for (int i = 0; i < graph.length; i++)
            {
                for (int j = 0; j < graph.length; j++)
                {
                    if (distances[i][k] != 0 && distances[k][j] != 0)
                    {
                        if (i!=j) {
                            if (distances[i][j] == 0 || (distances[i][j] > distances[i][k] + distances[k][j])) {
                                distances[i][j] = distances[i][k] + distances[k][j];
                            }
                        }
                    }
                }
            }
        }
    }

    private int getMaximumDistance() {
        int maxim = 0;
        for (int i=0; i<graph.length; i++)
            for (int j=0; j<graph.length; j++)
                if (distances[i][j] > maxim)
                    maxim = distances[i][j];
        return maxim;
    }

    private int getComponentLongestPath(int maximumDistance) {
        int component = 0;
        for (int i=0; i<graph.length; i++)
            for (int j=0; j<graph.length; j++)
                if (distances[i][j] == maximumDistance)
                    component = visited[i];
        return component;
    }
}
