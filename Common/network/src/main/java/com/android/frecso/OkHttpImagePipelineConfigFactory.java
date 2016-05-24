/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.android.frecso;

import android.content.Context;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import okhttp3.OkHttpClient;

/**
 * Factory for getting an {@link ImagePipelineConfig} that uses
 * {@link OkHttpNetworkFetcher}.
 */
public class OkHttpImagePipelineConfigFactory {

  public static ImagePipelineConfig.Builder newBuilder(Context context, OkHttpClient okHttpClient) {
    return ImagePipelineConfig.newBuilder(context)
        .setNetworkFetcher(new OkHttpNetworkFetcher(okHttpClient));
  }

  public static ImagePipelineConfig.Builder newBuilder(Context context) {
    return ImagePipelineConfig.newBuilder(context)
            .setNetworkFetcher(new OkHttpNetworkFetcher());
  }
}
