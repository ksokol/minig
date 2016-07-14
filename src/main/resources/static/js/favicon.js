(function() {
    var favicon = "\
        R0lGODlhEAAQAMZsABxNix1NjCZVjhxZmx9amyBamjdflD1il0BlmEFll0JmmDtrmTZsn0Rql\
        WdmZWhnZmpqaGxram5ubnBvbllzlSt+vFV1oXJycUd8rzKHwGN9nzeHwTaIwU+GtmWIqGqJr\
        WOSu3iOq2eUvVqYw1+Yv16axGKdxWOdxWKfymeex5qamoiguXSnypOhtYykt3WpzXiqzqKio\
        nqu0pCqw4mux6SpsaWpsaWqsaWqsp6tv5Swyamts5OyyqGyxJC20rGwsK6xtaa0xKu1wbS0t\
        Km8zbW7xam+zKjA0rK+yavA0r69vLLAzrTBzKzD1LPF067G2LTF07HI2MjGxLzL19DPzs/T1\
        dLU18vW3tLV19nX1dbY2dva2Nva2d3b2tvc3dfd39/f3+Hf3uLg3uLg3+Hh4eTi4eXi4OTj4\
        ubl5Ojl5Orn5evp5////////////////////////////////////////////////////////\
        ////////////////////////yH5BAEKAH8ALAAAAAAQABAAAAePgH+Cg4SFhoeIiYZkZ1pYj\
        1hWXmhghWJGJCUnJyYjLFdchWY0MCgZHBsyL1FdhWk8Tl8pFT5VR01hhWs6TB1TTy4gUEllh\
        WozSBgLHgwiS0RjlitCDQQDBR9BPVuFWSEUAgEBAAYaOVSFUi0JCAruBxZFSoUxQzs2Nzg3N\
        UA/KoYSJkB44OBBhAuKEiocFAgAOw";

    //http://stackoverflow.com/questions/9847580/how-to-detect-safari-chrome-ie-firefox-and-opera-browser
    // Firefox 1.0+
    var isFirefox = typeof InstallTrigger !== 'undefined';
    // Chrome 1+
    var isChrome = !!window.chrome && !!window.chrome.webstore;

    var docHead = document.getElementsByTagName('head')[0];
    var newLink = document.createElement('link');
    newLink.rel = 'shortcut icon';
    newLink.type = 'image/x-icon';

    if(isFirefox || isChrome) {
        newLink.href = 'data:image/png;base64,'+ favicon;
    } else {
        newLink.href = 'images/favicon.ico';
    }

    docHead.appendChild(newLink);
})();