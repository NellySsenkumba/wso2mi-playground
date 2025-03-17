import * as dmUtils from "./dm-utils";

/*
* title : "root",
* inputType : "JSON",
*/
interface Root {
code: number
}

/*
* title : "root",
* outputType : "JSON",
*/
interface OutputRoot {
data: {
code: number
}
}



/**
 * functionName : map_S_root_S_root
 * inputVariable : inputroot
*/
export function mapFunction(input: Root): OutputRoot {
    return {
        data: {
            code: input.code
        }
    };
}

