/*
 * Copyright 2014 Joao Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apm4all.tracy;

/**
 * Use TracyCallable and TracyFutureTask instead as illustrated in TracyConcurrentTest
 */
@Deprecated
public class TracyableFutureRequest {
	private boolean traced;
	private Object data;

	public TracyableFutureRequest(boolean traced, Object data) {
		super();
		this.traced = traced;
		this.data = data;
	}
	
	public boolean isTraced()	{
		return(traced);
	}

	public Object getData() {
		return data;
	}
}
