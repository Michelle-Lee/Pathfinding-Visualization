package sample;

import java.util.HashMap;

public class Theme {
    String theme;
    String background;
    String startColor;
    String endColor;
    String obstacleColor;
    String pathColor;
    String drawColor;
    HashMap<String, String[]> colorScheme = new HashMap<>();
    CircularLinkedList gradient = new CircularLinkedList();
    CircularLinkedList flash = new CircularLinkedList();
    String[] Plain;
    String[] SynthwavePlain;
    String[] Synthwave;
    String[] Sunset;
    String[] DBZ;
    String[] DBZ2;


    public Theme(String theme) {

        this.theme = theme;
        setColors();
        colorScheme.put("Plain", Plain);
        colorScheme.put("Synthwave Plain", SynthwavePlain);
        colorScheme.put("Synthwave", Synthwave);
        colorScheme.put("Sunset", Sunset);
        colorScheme.put("Dragon Ball Z Pixel", DBZ);
        colorScheme.put("Dragon Ball Z", DBZ2);

        background = colorScheme.get(theme)[0];
        drawColor = colorScheme.get(theme)[1];
        startColor = colorScheme.get(theme)[2];
        endColor = colorScheme.get(theme)[3];
        obstacleColor = colorScheme.get(theme)[4];
        pathColor = colorScheme.get(theme)[5];

        setGradient(colorScheme.get(theme));
    }

    public void setGradient(String[] colors) {
        for (int i = 6; i < colors.length; i++){
            gradient.add(colors[i]);
        }
        for (int i = colors.length - 1; i > 6; i--){
            gradient.add(colors[i]);
        }

        if(theme == "Dragon Ball Z " || theme == "Dragon Ball Z Pixel"){
            flash.add("#00abff");
            flash.add("#1c0ef9");
        }
        else {
            for (int i = 6; i < colors.length; i+=2){
                flash.add(colors[i]);
            }
        }

    }


    public void setColors() {
        // first 6 items: <background, draw outline, start, end, obstacle, path>
        Plain = new String[]{"#eb9d99","#DEDEDE","#6ebe44","#089abc","#000000","#6b4b9e",
                "#f8b195","#eb9d99","#d78d9e","#bd81a1","#9d77a0","#7b6e9a","#58668e","#355c7d"};
        SynthwavePlain = new String[]{"#000000","#2de2e6", "#ffe031","#f04579","#000000","#2de2e6",
                "#9d00c6","#843de0","#5f59f6","#0082ff","#00a3ff","#00beff","#00d6ff","#00ecfe","#00f6f5","#00ffed"};
        Synthwave = new String[]{"resources/Synthwave.jpeg","","#d30cb8","#5da4a6","#f5144a","#8c1eff",
                "#ffb700","#ffab00","#ff9e09","#ff9214","#ff851c","#ff7824","#ff6b2b","#fe5d31","#fe5336", "#fc483a","#fb3d3f","#fa3641","#f82845","#fb2d44"};
        Sunset = new String[]{"resources/sunset.jpg","","#ee4b4b","#313FF6","#2892FF","#FFEF8A",
                "#f7ce5b","#f9bd50","#faab48","#fb9a43","#fa8742","#f77443","#f36046","#ee4b4b"};
        DBZ = new String[]{"resources/DBZ_pixel.png", "","#f9e30e","#f90e0e","#ffff00","#0ef9f2",
                "#ffffff", "#eafffd", "#d4fefb", "#bdfef9", "#a3fdf8", "#85fcf6", "#5ffaf4", "#0ef9f2"};
        DBZ2 = new String[]{"resources/DBZ.png", "","#f9e30e","#f90e0e","#ffff00","#0ef9f2",
                "#ffffff", "#eafffd", "#d4fefb", "#bdfef9", "#a3fdf8", "#85fcf6", "#5ffaf4", "#0ef9f2"};
    }
}
//"#ffb700","#ffab00","#ff9e09","#ff9214","#ff851c",

//add reset button
//adjust obstacles