using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Cashlez.RNCashlez
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNCashlezModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNCashlezModule"/>.
        /// </summary>
        internal RNCashlezModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNCashlez";
            }
        }
    }
}
